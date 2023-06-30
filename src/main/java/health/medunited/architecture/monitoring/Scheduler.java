package health.medunited.architecture.monitoring;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.xml.ws.Holder;

import de.gematik.ws.conn.cardservice.v8.PinStatusEnum;
import de.gematik.ws.conn.cardservice.wsdl.v8.FaultMessage;
import de.gematik.ws.conn.connectorcommon.v5.Status;
import health.medunited.architecture.jaxrs.management.SecunetConnector;
import health.medunited.architecture.jaxrs.resource.MonitoringRequest;
import health.medunited.architecture.model.ManagementCredentials;
import org.eclipse.microprofile.metrics.*;
import org.eclipse.microprofile.metrics.Timer;
import org.eclipse.microprofile.metrics.annotation.RegistryType;

import de.gematik.ws.conn.cardservice.wsdl.v8.CardServicePortType;
import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;
import health.medunited.architecture.entities.RuntimeConfig;
import health.medunited.architecture.provider.ConnectorServicesProducer;
import health.medunited.architecture.service.common.security.SecretsManagerService;
import health.medunited.architecture.service.endpoint.EndpointDiscoveryService;

@Singleton
public class Scheduler {

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    private MetricRegistry applicationRegistry;

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    SecunetConnector secunetConnector;

    private static final Logger log = Logger.getLogger(Scheduler.class.getName());

    private static final String VERIFIABLE = "VERIFIABLE";
    private static final String VERIFIED = "VERIFIED";
    private static final String BLOCKED = "BLOCKED";
    private static final String DISABLED = "DISABLED";
    private static final String EMPTY_PIN = "EMPTY_PIN";
    private static final String TRANSPORT_PIN = "TRANSPORT_PIN";

    @Schedule(second = "*/15", minute = "*", hour = "*", persistent = false)
    public void monitorConnectors() {
        log.info("Scanning Connectors");

        TypedQuery<RuntimeConfig> query =
                entityManager.createNamedQuery("RuntimeConfig.findAll",
                        RuntimeConfig.class);

        List<RuntimeConfig> runtimeConfigs = query.getResultList();
        for (RuntimeConfig runtimeConfig : runtimeConfigs) {

            try {
                SecretsManagerService secretsManagerService = new SecretsManagerService();
                if (runtimeConfig.getClientCertificate() != null) {
                    String keystore = runtimeConfig.getClientCertificate();
                    String keystorePassword = runtimeConfig.getClientCertificatePassword();
                    try {
                        secretsManagerService.setUpSSLContext(secretsManagerService.getKeyFromKeyStoreUri(keystore, keystorePassword));
                    } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException |
                            CertificateException
                            | URISyntaxException | IOException e) {
                        log.log(Level.WARNING, "Could not create SSL context", e);
                    }
                }

                EndpointDiscoveryService endpointDiscoveryService = new EndpointDiscoveryService(secretsManagerService);
                try {
                    endpointDiscoveryService.obtainConfiguration(runtimeConfig.getUrl());
                } catch (Exception e) {
                    log.log(Level.SEVERE, e.getMessage());
                    continue;
                }

                ConnectorServicesProducer connectorServicesProducer = new ConnectorServicesProducer();
                connectorServicesProducer.setSecretsManagerService(secretsManagerService);
                connectorServicesProducer.setEndpointDiscoveryService(endpointDiscoveryService);

                connectorServicesProducer.initializeEventServicePortType();
                connectorServicesProducer.initializeCardServicePortType();

                EventServicePortType eventServicePortType = connectorServicesProducer.getEventServicePortType();
                CardServicePortType cardServicePortType = connectorServicesProducer.getCardServicePortType();

                secunetConnector.setSecretsManagerService(secretsManagerService);

                // register a new application scoped metric
                Timer connectorResponseTime = applicationRegistry.timer(Metadata.builder()
                        .withName("connectorResponseTime_" + runtimeConfig.getUrl())
                        .withDescription("Timer for measuring response times for " + runtimeConfig.getUrl())
                        .build());

                addMetricTimeInSecondsUntilSMCKTCardExpires(connectorResponseTime, runtimeConfig, eventServicePortType);

                addMetricCurrentlyConnectedCards(connectorResponseTime, runtimeConfig, eventServicePortType);

                addMetricPinStatusSMCB(VERIFIABLE, connectorResponseTime, runtimeConfig, eventServicePortType, cardServicePortType);
                addMetricPinStatusSMCB(VERIFIED, connectorResponseTime, runtimeConfig, eventServicePortType, cardServicePortType);
                addMetricPinStatusSMCB(BLOCKED, connectorResponseTime, runtimeConfig, eventServicePortType, cardServicePortType);
                addMetricPinStatusSMCB(DISABLED, connectorResponseTime, runtimeConfig, eventServicePortType, cardServicePortType);
                addMetricPinStatusSMCB(EMPTY_PIN, connectorResponseTime, runtimeConfig, eventServicePortType, cardServicePortType);
                addMetricPinStatusSMCB(TRANSPORT_PIN, connectorResponseTime, runtimeConfig, eventServicePortType, cardServicePortType);

                FileReader reader = new FileReader("./monitoring/MonitoringAspects.json");
                MonitoringRequest incomingMonitoring = JsonbBuilder.create().fromJson(reader, MonitoringRequest.class);

                if(incomingMonitoring.isUpdateConnectorsOn()) {
                    log.log(Level.INFO, "checking for updates is enabled in the json config");
                    addMetricIsKonnektorUpdated(runtimeConfig);
                } else {
                    log.log(Level.INFO, "checking for updates is disabled in the json config");
                }

            } catch (Throwable t) {
                log.log(Level.INFO, "Error while contacting connector", t);
            }
        }
    }

    private void addMetricTimeInSecondsUntilSMCKTCardExpires(Timer connectorResponseTime, RuntimeConfig runtimeConfig, EventServicePortType eventServicePortType) {
        try {
            Callable<Long> secondsCallable = getTimeInSecondsUntilSMCKTCardExpires(runtimeConfig, eventServicePortType);
            Gauge<Long> timeInSecondsUntilSMCKTCardExpires = applicationRegistry.gauge(Metadata.builder()
                    .withName("secondsDurationLeftUntilSMC_KTexpiry_" + runtimeConfig.getUrl())
                    .withDescription("duration of seconds until the SMC_KT card expires " + runtimeConfig.getUrl())
                    .build(), () -> {
                Long secondsDurationLeftUntilExpiryLng;
                try {
                    secondsDurationLeftUntilExpiryLng = connectorResponseTime.time(secondsCallable);
                    log.info("Currently connected card expires in sec: " + secondsDurationLeftUntilExpiryLng + " " + runtimeConfig.getUrl());
                    return secondsDurationLeftUntilExpiryLng;
                } catch (Exception e) {
                    log.log(Level.WARNING, "Cannot measure connector", e);
                }
                return null;
            });
        } catch (Exception e) {
            log.log(Level.WARNING, "Cannot measure connector", e);
        }
    }

    private Callable<Long> getTimeInSecondsUntilSMCKTCardExpires(RuntimeConfig runtimeConfig, EventServicePortType eventServicePortType) {
        return () -> {
            GetCards getCards = new GetCards();
            ContextType contextType = new ContextType();
            contextType.setMandantId(runtimeConfig.getMandantId());
            contextType.setClientSystemId(runtimeConfig.getClientSystemId());
            contextType.setWorkplaceId(runtimeConfig.getWorkplaceId());
            contextType.setUserId(runtimeConfig.getUserId());
            getCards.setContext(contextType);
            GetCardsResponse getCardsResponse = eventServicePortType.getCards(getCards);
            int numberOfCards = getCardsResponse.getCards().getCard().size();
            String expirationString;
            for (int i = 0; i < numberOfCards; i++) {
                String cardType = getCardsResponse.getCards().getCard().get(i).getCardType().toString();
                if (Objects.equals(cardType, "SMC_KT")) {
                    expirationString = getCardsResponse.getCards().getCard().get(i).getCertificateExpirationDate().toString();

                    String expStr = expirationString.substring(0, expirationString.length() - 1);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date future = sdf.parse(expStr);

                    Instant one = Instant.now();
                    Instant two = future.toInstant();
                    Duration duration = Duration.between(one, two);

                    return duration.toSeconds();
                }
            }
            //unlikely to reach
            return null;
        };
    }

    private void addMetricCurrentlyConnectedCards(Timer connectorResponseTime, RuntimeConfig runtimeConfig, EventServicePortType eventServicePortType) {
        try {
            Callable<Integer> callable = getCurrentlyConnectedCards(runtimeConfig, eventServicePortType);
            Gauge<Integer> currentlyConnectedCards = applicationRegistry.gauge(Metadata.builder()
                    .withName("currentlyConnectedCards_" + runtimeConfig.getUrl())
                    .withDescription("Currently connected cards for " + runtimeConfig.getUrl())
                    .build(), () -> {
                Integer currentlyConnectedCardsInt;
                try {
                    currentlyConnectedCardsInt = connectorResponseTime.time(callable);
                    log.info("Currently connected cards: " + currentlyConnectedCardsInt + " " + runtimeConfig.getUrl());
                    return currentlyConnectedCardsInt;
                } catch (Exception e) {
                    log.log(Level.WARNING, "Cannot measure connector", e);
                }
                return null;
            });
        } catch (Exception e) {
            log.log(Level.WARNING, "Cannot measure connector", e);
        }
    }

    private Callable<Integer> getCurrentlyConnectedCards(RuntimeConfig runtimeConfig, EventServicePortType eventServicePortType) {
        return () -> {
            GetCards getCards = new GetCards();
            ContextType contextType = new ContextType();
            contextType.setMandantId(runtimeConfig.getMandantId());
            contextType.setClientSystemId(runtimeConfig.getClientSystemId());
            contextType.setWorkplaceId(runtimeConfig.getWorkplaceId());
            contextType.setUserId(runtimeConfig.getUserId());
            getCards.setContext(contextType);
            GetCardsResponse getCardsResponse = eventServicePortType.getCards(getCards);
            return getCardsResponse.getCards().getCard().size();
        };
    }

    private void addMetricPinStatusSMCB(String typeOfStatus, Timer connectorResponseTime, RuntimeConfig runtimeConfig, EventServicePortType eventServicePortType, CardServicePortType cardServicePortType) {
        try {
            Callable<Integer> callable = getNrOfCardsWithStatus(typeOfStatus, runtimeConfig, eventServicePortType, cardServicePortType);
            Gauge<Integer> metricPinStatusSMCB = applicationRegistry.gauge(
                    Metadata.builder()
                            .withName("pinStatus_SMC_B_" + typeOfStatus + "_" + runtimeConfig.getUrl())
                            .withDescription("SMC_B cards with PinStatus " + typeOfStatus + " for " + runtimeConfig.getUrl())
                            .build(), () -> {
                        Integer nrOfCardsWithStatus;
                        try {
                            nrOfCardsWithStatus = connectorResponseTime.time(callable);
                            log.info("Cards with pin status " + typeOfStatus + " : " + nrOfCardsWithStatus + " " + runtimeConfig.getUrl());
                            return nrOfCardsWithStatus;
                        } catch (Exception e) {
                            log.log(Level.WARNING, "Cannot measure connector", e);
                        }
                        return null;
                    });
        } catch (Exception e) {
            log.log(Level.WARNING, "Cannot measure connector", e);
        }
    }

    int getIsConnectorUpdated(RuntimeConfig runtimeConfig) {
        return secunetConnector.checkUpdate(runtimeConfig.getUrl(), "8500",
                new ManagementCredentials(runtimeConfig.getUsername(), runtimeConfig.getPassword()));
    }


    private void addMetricIsKonnektorUpdated(RuntimeConfig runtimeConfig) {
        try {
            int isUpdated = getIsConnectorUpdated(runtimeConfig);
            applicationRegistry
                    .gauge(
                            Metadata.builder()
                                    .withName("isConnectorCurrentlyUpdated_")
                                    .withDescription("Shows if the Connector is updated to the newest possible Firmware version")
                                    .build(), () -> {
                                try {
                                    log.info("Is the connector currently updated?: " + isUpdated);
                                    return isUpdated;
                                } catch (Exception e) {
                                    log.log(Level.WARNING, "Cannot measure connector", e);
                                }
                                return null;
                            });
        } catch (Exception e) {
            log.log(Level.WARNING, "Cannot measure if connector is updated", e);
        }
    }

    private Callable<Integer> getNrOfCardsWithStatus(String typeOfStatus, RuntimeConfig runtimeConfig, EventServicePortType eventServicePortType, CardServicePortType cardServicePortType) {
        return () -> {
            GetCards getCards = new GetCards();
            ContextType contextType = new ContextType();
            contextType.setMandantId(runtimeConfig.getMandantId());
            contextType.setClientSystemId(runtimeConfig.getClientSystemId());
            contextType.setWorkplaceId(runtimeConfig.getWorkplaceId());
            contextType.setUserId(runtimeConfig.getUserId());
            getCards.setContext(contextType);
            GetCardsResponse getCardsResponse = eventServicePortType.getCards(getCards);

            int numberOfCards = getCardsResponse.getCards().getCard().size();
            int nrOfCardsWithStatus = 0;

            for (int i = 0; i < numberOfCards; i++) {
                String cardType = getCardsResponse.getCards().getCard().get(i).getCardType().toString();
                String cardHandle = getCardsResponse.getCards().getCard().get(i).getCardHandle();
                if (Objects.equals(cardType, "SMC_B")) {
                    String pinType = "PIN.SMC";
                    String pinStatus = getPinStatus(cardServicePortType, contextType, cardHandle, pinType);
                    if (pinStatus.equals(typeOfStatus)) {
                        nrOfCardsWithStatus += 1;
                    }
                }
            }
            return nrOfCardsWithStatus;
        };
    }

    private String getPinStatus(CardServicePortType cardServicePortType, ContextType contextType, String cardHandle, String pinType) throws FaultMessage {
        Holder<Status> status = new Holder<>();
        Holder<PinStatusEnum> pinStatusEnum = new Holder<>();
        Holder<BigInteger> leftTries = new Holder<>();

        cardServicePortType.getPinStatus(contextType, cardHandle, pinType, status, pinStatusEnum, leftTries);
        return pinStatusEnum.value.toString();
    }
}
