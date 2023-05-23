package health.medunited.architecture.monitoring;

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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.xml.ws.Holder;

import de.gematik.ws.conn.cardservice.v8.PinStatusEnum;
import de.gematik.ws.conn.connectorcommon.v5.Status;
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

    private static Logger log = Logger.getLogger(Scheduler.class.getName());

    @Schedule(second = "*/15", minute = "*", hour = "*", persistent = false)
    public void monitorConnectors() {
        log.info("Scanning Connectors");

        TypedQuery<RuntimeConfig> query =
            entityManager.createNamedQuery("RuntimeConfig.findAll",
                RuntimeConfig.class);

        List<RuntimeConfig> runtimeConfigs = query.getResultList();
        for(RuntimeConfig runtimeConfig : runtimeConfigs) {

            try {
                SecretsManagerService secretsManagerService = new SecretsManagerService();
                if(runtimeConfig.getClientCertificate() != null) {
                    String keystore = runtimeConfig.getClientCertificate();
                    String keystorePassword = runtimeConfig.getClientCertificatePassword();
                    try {
                        secretsManagerService.setUpSSLContext(secretsManagerService.getKeyFromKeyStoreUri(keystore, keystorePassword));
                    } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | CertificateException
                            | URISyntaxException | IOException e) {
                        log.log(Level.WARNING, "Could not create ssl context", e);
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

                // register a new application scoped metric
                Timer connectorResponseTime = applicationRegistry.timer(Metadata.builder()
                    .withName("connectorResponseTime_"+runtimeConfig.getUrl())
                    .withDescription("Timer for measuring response times for "+runtimeConfig.getUrl())
                    .build());



                Callable<Long> secondsCallable = () -> {
                    GetCards getCards = new GetCards();
                    ContextType contextType = new ContextType();
                    contextType.setMandantId(runtimeConfig.getMandantId());
                    contextType.setClientSystemId(runtimeConfig.getClientSystemId());
                    contextType.setWorkplaceId(runtimeConfig.getWorkplaceId());
                    contextType.setUserId(runtimeConfig.getUserId());
                    getCards.setContext(contextType);
                    GetCardsResponse getCardsResponse = eventServicePortType.getCards(getCards);
                    Integer numberOfCards = getCardsResponse.getCards().getCard().size();
                    String expirationString = "2024-01-01";
                    for(int i=0;i<numberOfCards;i++){
                        String cardType = getCardsResponse.getCards().getCard().get(i).getCardType().toString();
                        if (cardType == "SMC_KT") {
                            expirationString  = getCardsResponse.getCards().getCard().get(i).getCertificateExpirationDate().toString();

                            String expStr = expirationString.substring(0,expirationString.length()-1);


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



                try {
                    Gauge<Long> someVar  = applicationRegistry.gauge(Metadata.builder()
                            .withName("secondsDurationLeftUntilSMC_KTexpiry_"+runtimeConfig.getUrl())
                            .withDescription("duration of seconds until the SMC_KT card expires "+runtimeConfig.getUrl())
                            .build(), () -> {
                        Long secondsDurationLefTillExpiryLng;
                        try {
                            secondsDurationLefTillExpiryLng = connectorResponseTime.time(secondsCallable);
                            log.info("Currently connected card expires in sec: "+secondsDurationLefTillExpiryLng+" "+runtimeConfig.getUrl());
                            return secondsDurationLefTillExpiryLng;
                        } catch (Exception e) {
                            log.log(Level.WARNING, "Can't measure connector", e);
                        }
                        return null;
                    });
                } catch (Exception e) {
                    log.log(Level.WARNING, "Can't measure connector", e);
                }


                addMetricsPinStatusSMCB("VERIFIABLE", runtimeConfig, eventServicePortType, cardServicePortType);
                addMetricsPinStatusSMCB("VERIFIED", runtimeConfig, eventServicePortType, cardServicePortType);
                addMetricsPinStatusSMCB("BLOCKED", runtimeConfig, eventServicePortType, cardServicePortType);
                addMetricsPinStatusSMCB("DISABLED", runtimeConfig, eventServicePortType, cardServicePortType);
                addMetricsPinStatusSMCB("EMPTY_PIN", runtimeConfig, eventServicePortType, cardServicePortType);
                addMetricsPinStatusSMCB("TRANSPORT_PIN", runtimeConfig, eventServicePortType, cardServicePortType);


                Callable<Integer> callable = () -> {
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



                try {
                    Gauge<Integer> currentlyConnectedCards  = applicationRegistry.gauge(Metadata.builder()
                    .withName("currentlyConnectedCards_"+runtimeConfig.getUrl())
                    .withDescription("Currently connected cards for "+runtimeConfig.getUrl())
                    .build(), () -> {
                        Integer currentlyConnectedCardsInt;
                        try {
                            currentlyConnectedCardsInt = connectorResponseTime.time(callable);
                            log.info("Currently connected cards: "+currentlyConnectedCardsInt+" "+runtimeConfig.getUrl());
                            return currentlyConnectedCardsInt;
                        } catch (Exception e) {
                            log.log(Level.WARNING, "Can't measure connector", e);
                        }
                        return null;
                    });
                } catch (Exception e) {
                    log.log(Level.WARNING, "Can't measure connector", e);
                }


            } catch(Throwable t) {
                log.log(Level.INFO, "Error while contacting connector", t);
            }
        }
    }

    private void addMetricsPinStatusSMCB(String typeOfStatus, RuntimeConfig runtimeConfig, EventServicePortType eventServicePortType, CardServicePortType cardServicePortType) {
        try {
            Gauge<Integer> metricPinStatusSMCB = applicationRegistry.register(
                    Metadata.builder()
                            .withName("pinStatus_SMC-B_" + typeOfStatus + "_" + runtimeConfig.getUrl())
                            .withDescription("pinStatus_SMC-B_" + typeOfStatus + "_" + runtimeConfig.getUrl())
                            .withType(MetricType.GAUGE)
                            .build(),
                    () -> getNumberOfSMCBCardsWithStatus(typeOfStatus, runtimeConfig, eventServicePortType, cardServicePortType)
            );
        } catch (Exception e) {
            log.log(Level.WARNING, "Can't measure connector", e);
        }
    }

    private Integer getNumberOfSMCBCardsWithStatus(String typeOfStatus, RuntimeConfig runtimeConfig, EventServicePortType eventServicePortType, CardServicePortType cardServicePortType) {
        try {
            int nrOfCardsWithStatus = 0;

            GetCards getCards = new GetCards();
            ContextType contextType = new ContextType();
            contextType.setMandantId(runtimeConfig.getMandantId());
            contextType.setClientSystemId(runtimeConfig.getClientSystemId());
            contextType.setWorkplaceId(runtimeConfig.getWorkplaceId());
            contextType.setUserId(runtimeConfig.getUserId());
            getCards.setContext(contextType);
            GetCardsResponse getCardsResponse = eventServicePortType.getCards(getCards);

            int numberOfCards = getCardsResponse.getCards().getCard().size();

            for (int i = 0; i < numberOfCards; i++) {
                String cardType = getCardsResponse.getCards().getCard().get(i).getCardType().toString();
                String cardHandle = getCardsResponse.getCards().getCard().get(i).getCardHandle();
                if (Objects.equals(cardType, "SMC_B")) {
                    String pinType = "PIN.SMC";
                    Holder<Status> status = new Holder<>();
                    Holder<PinStatusEnum> pinStatusEnum = new Holder<>();
                    Holder<BigInteger> leftTries = new Holder<>();

                    cardServicePortType.getPinStatus(contextType, cardHandle, pinType, status, pinStatusEnum, leftTries);
                    String pinStatus = pinStatusEnum.value.toString();

                    if (pinStatus.equals(typeOfStatus)) {
                        nrOfCardsWithStatus += 1;
                    }
                }
            }
            return nrOfCardsWithStatus;
        } catch (Exception e) {
            log.log(Level.WARNING, "Can't measure connector", e);
        }
        return null;
    }
}
