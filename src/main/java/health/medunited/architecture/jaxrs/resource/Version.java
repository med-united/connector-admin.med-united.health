package health.medunited.architecture.jaxrs.resource;
package health.medunited.architecture.jaxrs.resource;


import health.medunited.architecture.service.endpoint.EndpointDiscoveryService;
import java.util.logging.Logger;


import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

import health.medunited.architecture.service.endpoint.EndpointDiscoveryService;


@RequestScoped
@RequestScoped
@Path("productTypeInformation")
@Path("productTypeInformation")
public class Version {
public class Version {
    private static final Logger log = Logger.getLogger(Version.class.getName());
    private static final Logger log = Logger.getLogger(Version.class.getName());
    @Inject
    @Inject
    EndpointDiscoveryService endpointDiscoveryService;
    EndpointDiscoveryService endpointDiscoveryService;
    @GET
    @GET
    @Path("/getVersion")
    @Path("/getVersion")
    public Response getProductTypeInformation(@HeaderParam("X-Host") String connectorUrl) {
    public Response getProductTypeInformation(@HeaderParam("X-Host") String connectorUrl) {
        endpointDiscoveryService.obtainConfiguration(connectorUrl);
        endpointDiscoveryService.obtainConfiguration(connectorUrl);
        return Response.ok(endpointDiscoveryService.getConnectorVersion()).build();
        return Response.ok(endpointDiscoveryService.getConnectorVersion()).build();
    }
    }
}
