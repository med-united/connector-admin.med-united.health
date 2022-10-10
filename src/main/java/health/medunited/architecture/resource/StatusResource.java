package health.medunited.architecture.resource;

import health.medunited.architecture.context.ConnectorScopeContext;
import health.medunited.architecture.exception.connector.ConnectorCardsException;
import health.medunited.architecture.model.CardHandleType;
import health.medunited.architecture.provider.ConnectorScope;
import health.medunited.architecture.provider.MultiConnectorServicesProvider;
import health.medunited.architecture.service.CardService;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Path("status")
public class StatusResource {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(StatusResource.class.getName());

    @Inject
    CardService cardService;

    @Inject
    Provider<ConnectorScope> connectorScopeProvider;

    @Inject
    MultiConnectorServicesProvider multiConnectorServicesProvider;

    @GET
    public String getStatus(@HeaderParam("Url") String url,
                            @HeaderParam("MandantId") String mandantId,
                            @HeaderParam("ClientSystemId") String clientSystemId,
                            @HeaderParam("WorkplaceId") String workplaceId,
                            @HeaderParam("UserId") String userId,
                            @HeaderParam("BasicAuthUsername") String basicAuthUsername,
                            @HeaderParam("BasicAuthPassword") String basicAuthPassword,
                            @HeaderParam("ClientCertificate") String clientCertificate,
                            @HeaderParam("ClientCertificatePassword") String clientCertificatePassword) throws ConnectorCardsException, IOException, ParserConfigurationException {
        ConnectorScope connectorScope = connectorScopeProvider.get();

        ConnectorScopeContext connectorScopeContext = new ConnectorScopeContext(url, mandantId, clientSystemId,
                workplaceId, userId, basicAuthUsername, basicAuthPassword, clientCertificate, clientCertificatePassword);

        connectorScope.setConnectorScopeContext(connectorScopeContext);

        // connectorServicesProvider = new SingleConnectorServicesProvider(connectorScopeContext);

        //EndpointDiscoveryService endpointDiscoveryService = new EndpointDiscoveryService(connectorScopeContext, secretsManagerService);

        //endpointDiscoveryService.obtainConfiguration(false);

        cardService.setConnectorServicesProvider(multiConnectorServicesProvider);

        String smcbHandle = cardService.getConnectorCardHandle(CardHandleType.SMC_B, connectorScopeContext);

        return cardService.getCardStatus();
    }
}

