package health.medunited.architecture.jaxrs.resource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import health.medunited.architecture.service.endpoint.EndpointDiscoveryService;

@Path("sds")
public class ConnectorSds {

    @Inject
    EndpointDiscoveryService endpointDiscoveryService;

    @GET
    @Path("config")
    public Response connectorSdsConfig(@HeaderParam("X-Host") String connectorUrl) {
        try {
            return Response
                .status(Response.Status.OK)
                .entity(endpointDiscoveryService.obtainConfiguration(connectorUrl))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }

    @GET
    @Path("file")
    public byte[] connectorSdsFile(@HeaderParam("X-Host") String connectorBaseUrl) {
        return endpointDiscoveryService.obtainFile(connectorBaseUrl);
    }

}