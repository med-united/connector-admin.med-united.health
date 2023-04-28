package health.medunited.architecture.jaxrs.resource;

import de.gematik.ws.conn.servicedirectory.v3.ConnectorServices;
import health.medunited.architecture.service.endpoint.EndpointDiscoveryService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("sds")
public class ConnectorSds {

    @Inject
    EndpointDiscoveryService endpointDiscoveryService;

    @GET
    public ConnectorServices connectorSds(@QueryParam("connectorUrl") String connectorUrl) {
        endpointDiscoveryService.obtainConfiguration(connectorUrl);
        return endpointDiscoveryService.getConnectorSds();
    }
}