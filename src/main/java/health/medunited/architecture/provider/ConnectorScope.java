package health.medunited.architecture.provider;

import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import health.medunited.architecture.context.ConnectorScopeContext;
import health.medunited.architecture.service.CardService;
import health.medunited.architecture.service.StatusService;
import health.medunited.architecture.z.DefaultConnectorServicesProvider;
import health.medunited.architecture.z.SecretsManagerService;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

@RequestScoped
public class ConnectorScope implements Callable<Void> {

    private static final Logger logger = java.util.logging.Logger.getLogger(ConnectorScope.class.getName());

    @Context
    HttpServletRequest httpServletRequest;

    @Override
    public Void call() throws Exception {
        return null;
    }

    //set the httpServletRequest
    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Produces
    @RequestScoped
    public StatusService createStatusService(SecretsManagerService secretsManagerService,
                                             DefaultConnectorServicesProvider defaultConnectorServicesProvider) {
        ContextType contextType = new ContextType();
        contextType.setClientSystemId(httpServletRequest.getHeader("ClientSystemId"));
        contextType.setMandantId(httpServletRequest.getHeader("MandantId"));
        contextType.setWorkplaceId(httpServletRequest.getHeader("WorkplaceId"));
        contextType.setUserId(httpServletRequest.getHeader("UserId"));

        return new StatusService(
                contextType,
                httpServletRequest.getHeader("Url"),
                httpServletRequest.getHeader("ClientCertificate"),
                httpServletRequest.getHeader("ClientCertificatePassword"),
                secretsManagerService,
                defaultConnectorServicesProvider);
    }
}



