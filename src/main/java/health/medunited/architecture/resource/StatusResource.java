package health.medunited.architecture.resource;

import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.FaultMessage;
import health.medunited.architecture.model.RuntimeConfig;
import health.medunited.architecture.service.StatusService;
import health.medunited.architecture.util.RuntimeConfigExtractor;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("status")
public class StatusResource {

    @Context
    HttpServletRequest httpServletRequest;

    @Inject
    StatusService statusService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response status(@HeaderParam("Content-Type") String contentType,
                           @HeaderParam("x-client-system-id") String clientSystemId,
                           @HeaderParam("x-client-certificate") String clientCertificate,
                           @HeaderParam("x-client-certificate-password") String clientCertificatePassword,
                           @HeaderParam("x-sign-port") String port,
                           @HeaderParam("x-vzd-port") String vzdPort,
                           @HeaderParam("x-mandant-id") String mandantId,
                           @HeaderParam("x-workplace-id") String workplaceId,
                           @HeaderParam("x-user-id") String userId,
                           @HeaderParam("x-host") String host) throws FaultMessage {

        RuntimeConfig runtimeConfig = RuntimeConfigExtractor.extractRuntimeConfigFromHeaders(
                clientSystemId, clientCertificate, clientCertificatePassword, host, port, vzdPort, mandantId, workplaceId, userId);
        GetCardsResponse response = statusService.getStatus(runtimeConfig);

        return Response.ok(response).build();
    }

}
