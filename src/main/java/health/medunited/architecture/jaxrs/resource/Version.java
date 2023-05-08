package health.medunited.architecture.jaxrs.resource;

import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import health.medunited.architecture.service.endpoint.EndpointDiscoveryService;

@RequestScoped
@Path("productTypeInformation")
public class Version {

    private static final Logger log = Logger.getLogger(Version.class.getName());

    @Inject
    EndpointDiscoveryService endpointDiscoveryService;

    @GET
    @Path("/getVersion")
    public Response getProductTypeInformation(@HeaderParam("X-Host") String connectorUrl) throws Exception {
        endpointDiscoveryService.obtainConfiguration(connectorUrl);
        return Response.ok(endpointDiscoveryService.getProductTypeInformation()).build();
    }
}
