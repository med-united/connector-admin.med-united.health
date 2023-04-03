package health.medunited.architecture.jaxrs.resource;

import de.gematik.ws._int.version.productinformation.v1.ProductTypeInformation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@RequestScoped
@Path("productTypeInformation")
public class Version {

    private static final Logger log = Logger.getLogger(Version.class.getName());

    @Context
    HttpServletRequest httpServletRequest;

    @Inject
    ProductTypeInformation productTypeInformation;

    @GET
    @Path("/getVersion")
    public Response getProductTypeInformation() {
        return Response.ok(productTypeInformation.getProductTypeVersion()).build();
    }
}
