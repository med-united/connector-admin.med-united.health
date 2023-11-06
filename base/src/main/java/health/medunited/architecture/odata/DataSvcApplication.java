package health.medunited.architecture.odata;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;

import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.core.rest.ODataExceptionMapperImpl;
import org.apache.olingo.odata2.core.rest.ODataRootLocator;
import org.apache.olingo.odata2.core.rest.app.AbstractODataApplication;

import health.medunited.architecture.CORSFilter;
import health.medunited.architecture.odata.etag.ETagResponseFilter;

/**
 * Configures a JAX-RS endpoint. Delete this class, if you are not exposing JAX-RS resources in your
 * application.
 *
 * @author airhacks.com
 */
@ApplicationPath("Data.svc")
public class DataSvcApplication extends AbstractODataApplication {

  private final Set<Object> singletons = new HashSet<>();

  public DataSvcApplication() {
    singletons.add(new CORSFilter());
  }

  @Override
  public Set<Class<?>> getClasses() {
    Set<Class<?>> set = new HashSet<>();
    set.add(ODataRootLocator.class);
    set.add(ODataExceptionMapperImpl.class);
    set.add(ETagResponseFilter.class);
    return set;
  }

  @Override
  public Class<? extends ODataServiceFactory> getServiceFactoryClass() {
    return JpaODataServiceFactory.class;
  }

  @Override
  public Set<Object> getSingletons() {
    return singletons;
  }
}
