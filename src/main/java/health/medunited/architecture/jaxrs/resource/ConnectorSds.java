package health.medunited.architecture.jaxrs.resource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import de.gematik.ws.conn.servicedirectory.v3.ConnectorServices;
import health.medunited.architecture.service.endpoint.EndpointDiscoveryService;

@Path("sds")
public class ConnectorSds {

    @Inject
    EndpointDiscoveryService endpointDiscoveryService;

    @GET
    @Path("config")
    public ConnectorServices connectorSdsConfig(@QueryParam("connectorUrl") String connectorUrl) throws Throwable {
        endpointDiscoveryService.obtainConfiguration(connectorUrl);
        return endpointDiscoveryService.getConnectorSds();
    }

    @GET
    @Path("file")
    public byte[] connectorSdsFile(@HeaderParam("X-Host") String connectorBaseUrl) {
        return endpointDiscoveryService.obtainFile(connectorBaseUrl);
    }
}