package health.medunited.architecture.monitoring;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
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

import health.medunited.architecture.service.endpoint.EndpointDiscoveryService;
import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Timer;
import org.eclipse.microprofile.metrics.annotation.RegistryType;

import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;
import health.medunited.architecture.entities.RuntimeConfig;
import health.medunited.architecture.provider.ConnectorServicesProducer;
import health.medunited.architecture.service.common.security.SecretsManagerService;
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

                EndpointDiscoveryService endpointDiscoveryService = new EndpointDiscoveryService();
                endpointDiscoveryService.setSecretsManagerService(secretsManagerService);
                endpointDiscoveryService.obtainConfiguration(runtimeConfig.getUrl());

                ConnectorServicesProducer connectorServicesProducer = new ConnectorServicesProducer();
                connectorServicesProducer.setSecretsManagerService(secretsManagerService);
                connectorServicesProducer.setEndpointDiscoveryService(endpointDiscoveryService);

                connectorServicesProducer.initializeEventServicePortType();

                EventServicePortType eventServicePortType = connectorServicesProducer.getEventServicePortType();

                // register a new application scoped metric
                Timer connectorResponseTime = applicationRegistry.timer(Metadata.builder()
                    .withName("connectorResponseTime_"+runtimeConfig.getUrl())
                    .withDescription("Timer for measuring response times for "+runtimeConfig.getUrl())
                    .build());

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
                
                Integer currentlyConnectedCardsInt;
                try {
                    currentlyConnectedCardsInt = connectorResponseTime.time(callable);
                    Gauge<Integer> currentlyConnectedCards  = applicationRegistry.gauge(Metadata.builder()
                        .withName("currentlyConnectedCards_"+runtimeConfig.getUrl())
                        .withDescription("Currently connected cards for "+runtimeConfig.getUrl())
                        .build(), () -> {
                            return currentlyConnectedCardsInt;
                        });
                    log.info("Currently connected cards: "+currentlyConnectedCards.getValue()+" "+runtimeConfig.getUrl());
                } catch (Exception e) {
                    log.log(Level.WARNING, "Can't measure connector", e);
                }
            } catch(Throwable t) {
                log.log(Level.INFO, "Error while contacting connector", t);
            }
        }
    }
}
