package health.medunited.architecture.jaxrs.resource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import de.gematik.ws.conn.servicedirectory.v3.ConnectorServices;
import health.medunited.architecture.service.endpoint.EndpointDiscoveryService;

@Path("sds")
public class ConnectorSds {

    @Inject
    EndpointDiscoveryService endpointDiscoveryService;

    @GET
    @Path("config")
    public ConnectorServices connectorSdsConfig(@QueryParam("connectorUrl") String connectorUrl) {
        endpointDiscoveryService.obtainConfiguration(connectorUrl);
        return endpointDiscoveryService.getConnectorSds();
    }

    @GET
    @Path("file")
    public byte[] connectorSdsFile(@HeaderParam("X-Host") String connectorBaseUrl) {
        return endpointDiscoveryService.obtainFile(connectorBaseUrl);
    }

    @GET
    @Path("connectorSpecifications")
    public Response getProductTypeInformation(@HeaderParam("X-Host") String connectorUrl) {
        endpointDiscoveryService.obtainConfiguration(connectorUrl);
        return Response.ok(endpointDiscoveryService.getConnectorProductInformation()).build();
    }
}