package health.medunited.architecture.provider;

import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import health.medunited.architecture.service.StatusService;

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
    public Void call() {
        return null;
    }

    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    /*@Produces
    @RequestScoped
    public StatusService createStatusService(DefaultConnectorServicesProvider defaultConnectorServicesProvider) {
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
                defaultConnectorServicesProvider);
    }*/
}



