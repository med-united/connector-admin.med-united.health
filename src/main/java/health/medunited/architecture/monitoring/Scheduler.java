package health.medunited.architecture.monitoring;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
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
import health.medunited.architecture.jaxrs.resource.Event;

import static health.medunited.architecture.provider.ContextTypeProducer.copyValuesFromProxyIntoContextType;

@Singleton
public class Scheduler {
    
    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    private MetricRegistry applicationRegistry;

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    Event event;

    private static Logger log = Logger.getLogger(Scheduler.class.getName());

    @Schedule(second = "*/15", minute = "*", hour = "*", persistent = false)
    public void monitorConnectors() throws Throwable {
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

                EndpointDiscoveryService endpointDiscoveryService = new EndpointDiscoveryService();
                endpointDiscoveryService.setSecretsManagerService(secretsManagerService);
                endpointDiscoveryService.obtainConfiguration(runtimeConfig.getUrl());

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



                Callable<Float> daydiffCallable = () -> {
                    GetCards getCards = new GetCards();
                    ContextType contextType = new ContextType();
                    contextType.setMandantId(runtimeConfig.getMandantId());
                    contextType.setClientSystemId(runtimeConfig.getClientSystemId());
                    contextType.setWorkplaceId(runtimeConfig.getWorkplaceId());
                    contextType.setUserId(runtimeConfig.getUserId());
                    getCards.setContext(contextType);
                    GetCardsResponse getCardsResponse = eventServicePortType.getCards(getCards);
                    Integer numberOfCards = getCardsResponse.getCards().getCard().size();
                    String expirationString = null;
                    for(int i=0;i<numberOfCards;i++){
                        String cardType = getCardsResponse.getCards().getCard().get(i).getCardType().toString();
                        if (cardType == "SMC_KT") {
                            expirationString  = getCardsResponse.getCards().getCard().get(i).getCertificateExpirationDate().toString();
                            Date expirationDate = new SimpleDateFormat("yyyy-MM-dd").parse(expirationString);

                            ZonedDateTime now = ZonedDateTime.now();
                            ZonedDateTime futureExpiration = expirationDate.toInstant().atZone(ZoneId.of("Europe/Berlin"));
                            Duration duration = Duration.between(now,futureExpiration);
                            Float daysDuration = (float) duration.toSeconds()/(24*60*60);
                            return daysDuration;
                        }
                    }
                    //unlikely to reach
                    return null;
                };

                try {
                    Gauge<Float> daysLefTillExpiry  = applicationRegistry.gauge(Metadata.builder()
                            .withName("daysLeftUntilSMC_KTexpiry_"+runtimeConfig.getUrl())
                            .withDescription("Number of days left until the SMC_KT card expires "+runtimeConfig.getUrl())
                            .build(), () -> {
                        Float daysLefTillExpiryFlt;
                        try {
                            daysLefTillExpiryFlt = connectorResponseTime.time(daydiffCallable);
                            log.info("Currently connected cards: "+daysLefTillExpiryFlt+" "+runtimeConfig.getUrl());
                            return daysLefTillExpiryFlt;
                        } catch (Exception e) {
                            log.log(Level.WARNING, "Can't measure connector", e);
                        }
                        return null;
                    });
                } catch (Exception e) {
                    log.log(Level.WARNING, "Can't measure connector", e);
                }










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
}
