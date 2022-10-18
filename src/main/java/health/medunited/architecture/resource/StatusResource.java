package health.medunited.architecture.resource;

import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import health.medunited.architecture.context.ConnectorScopeContext;
import health.medunited.architecture.exception.connector.ConnectorCardsException;
import health.medunited.architecture.model.CardHandleType;
import health.medunited.architecture.provider.ConnectorScope;
import health.medunited.architecture.service.CardService;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Path("test")
public class StatusResource {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(StatusResource.class.getName());

    @Inject
    CardService cardService;

    @Inject
    Provider<ConnectorScope> connectorScopeProvider;

    @GET
    public String getStatus(@HeaderParam("Url") String url,
                            @HeaderParam("MandantId") String mandantId,
                            @HeaderParam("ClientSystemId") String clientSystemId,
                            @HeaderParam("WorkplaceId") String workplaceId,
                            @HeaderParam("UserId") String userId,
                            @HeaderParam("ClientCertificate") String clientCertificate,
                            @HeaderParam("ClientCertificatePassword") String clientCertificatePassword) throws ConnectorCardsException, IOException, ParserConfigurationException {
        ConnectorScope connectorScope = connectorScopeProvider.get();

        ContextType contextType = new ContextType();
        contextType.setClientSystemId(clientSystemId);
        contextType.setMandantId(mandantId);
        contextType.setWorkplaceId(workplaceId);
        contextType.setUserId(userId);

        ConnectorScopeContext connectorScopeContext = new ConnectorScopeContext(url, contextType, clientCertificate, clientCertificatePassword);

        connectorScope.setConnectorScopeContext(connectorScopeContext);

        return cardService.getCardStatus();
    }
}

