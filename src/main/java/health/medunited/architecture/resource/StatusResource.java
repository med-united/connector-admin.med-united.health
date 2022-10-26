package health.medunited.architecture.resource;

import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.eventservice.wsdl.v7.FaultMessage;
import health.medunited.architecture.context.ConnectorScopeContext;
import health.medunited.architecture.model.RuntimeConfig;
import health.medunited.architecture.provider.ConnectorScope;
import health.medunited.architecture.service.StatusService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Objects;

@Path("status")
public class StatusResource {

    @Context
    HttpServletRequest httpServletRequest;

    @Inject
    StatusService statusService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response status() throws FaultMessage {

        statusService.getStatus(Objects.requireNonNull(extractRuntimeConfigFromHeaders()));

        return Response.ok().build();
    }

    private RuntimeConfig extractRuntimeConfigFromHeaders() {
        for(Object name : Collections.list(httpServletRequest.getHeaderNames())) {
            if(name.toString().startsWith("X-")) {
                return new RuntimeConfig(httpServletRequest);
            }
        }
        return null;
    }

}
