package health.medunited.architecture.jaxrs;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import health.medunited.architecture.jaxrs.resource.Card;
import health.medunited.architecture.jaxrs.resource.Certificate;
import health.medunited.architecture.jaxrs.resource.ConnectorManagement;
import health.medunited.architecture.jaxrs.resource.ConnectorSds;
import health.medunited.architecture.jaxrs.resource.Event;
import health.medunited.architecture.jaxrs.resource.Metrics;
import health.medunited.architecture.jaxrs.resource.Scheduler;
import health.medunited.architecture.jaxrs.resource.UpdateManagement;

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
		set.add(ConnectorSds.class);
		set.add(ConnectorManagement.class);
		set.add(UpdateManagement.class);
		return set;
	}
}
