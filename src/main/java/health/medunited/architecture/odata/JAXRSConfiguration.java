package health.medunited.architecture.odata;
import health.medunited.architecture.odata.etag.ETagResponseFilter;
import health.medunited.architecture.resource.StatusResource;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.core.rest.ODataExceptionMapperImpl;
import org.apache.olingo.odata2.core.rest.ODataRootLocator;
import org.apache.olingo.odata2.core.rest.app.AbstractODataApplication;

import javax.ws.rs.ApplicationPath;
import java.util.HashSet;
import java.util.Set;

/**
 * Configures a JAX-RS endpoint. Delete this class, if you are not exposing
 * JAX-RS resources in your application.
 *
 * @author airhacks.com
 */
@ApplicationPath("Data.svc")
public class JAXRSConfiguration extends AbstractODataApplication {
	public Set<Class<?>> getClasses() {
		Set<Class<?>> set = new HashSet<>();
		set.add(ODataRootLocator.class);
		set.add(ODataExceptionMapperImpl.class);
		set.add(ETagResponseFilter.class);
		set.add(StatusResource.class);
		return set;
	}

	@Override
	public Class<? extends ODataServiceFactory> getServiceFactoryClass() {
		return JpaODataServiceFactory.class;
	}
}
