package health.medunited.architecture.resource;

import health.medunited.architecture.context.ConnectorScopeContext;
import health.medunited.architecture.provider.ConnectorScope;
import health.medunited.architecture.service.CardService;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

@Path("status")
public class StatusResource {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(StatusResource.class.getName());

    @Inject
    CardService cardService;

    @Inject
    Provider<ConnectorScope> connectorScopeProvider;

    @GET
    public String getStatus(@HeaderParam("Url") String url,
                            @HeaderParam("TerminalId") String terminalId,
                            @HeaderParam("TerminalPassword") String terminalPassword) {
        ConnectorScope connectorScope = connectorScopeProvider.get();
        connectorScope.setConnectorScopeContext(new ConnectorScopeContext(url, terminalId));
        return "OK";
    }

}

