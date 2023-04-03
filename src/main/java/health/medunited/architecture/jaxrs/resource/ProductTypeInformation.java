package health.medunited.architecture.jaxrs.resource;

import de.gematik.ws.conn.connectorcontext.v2.ContextType;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequestScoped
@Path("productTypeInformation")
public class ProductTypeInformation {

    private static final Logger log = Logger.getLogger(ProductTypeInformation.class.getName());

    @Context
    HttpServletRequest httpServletRequest;

    @Inject
    ContextType contextType;

    @GET
    @Path("/get-product-type-information")
    public de.gematik.ws._int.version.productinformation.v1.ProductTypeInformation getProductTypeInformation() throws Throwable {
        try {

        } catch(Throwable t) {
            log.log(Level.WARNING, "Could not get product type information", t);
            throw t;
        }
        return null;
    }
}
