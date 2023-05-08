package health.medunited.architecture.jaxrs;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import health.medunited.architecture.jaxrs.resource.*;

@ApplicationPath("/connector")
public class ConnectorApplication extends Application {
    @Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> set = new HashSet<>();
		set.add(Event.class);
		set.add(Scheduler.class);
		set.add(Metrics.class);
		set.add(Certificate.class);
		set.add(Card.class);
		set.add(Version.class);
		set.add(VendorAndProduct.class);
		set.add(ConnectorSds.class);
		set.add(ConnectorManagement.class);
		return set;
	}
}
