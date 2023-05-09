package health.medunited.architecture.jaxrs.resource;

import de.gematik.ws._int.version.productinformation.v1.ProductIdentification;
import health.medunited.architecture.service.endpoint.EndpointDiscoveryService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@RequestScoped
@Path("productSpecifications")
public class Product {

    private static final Logger log = Logger.getLogger(Version.class.getName());

    @Inject
    EndpointDiscoveryService endpointDiscoveryService;

    @GET
    @Path("/getConnectorSpecifications")
    public Response getProductTypeInformation(@HeaderParam("X-Host") String connectorUrl) {
        endpointDiscoveryService.obtainConfiguration(connectorUrl);
        return Response.ok(endpointDiscoveryService.getConnectorProductInformation()).build();
    }

}
