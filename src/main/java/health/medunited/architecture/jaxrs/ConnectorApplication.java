package health.medunited.architecture.jaxrs;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import health.medunited.architecture.jaxrs.resource.Event;
import health.medunited.architecture.jaxrs.resource.Metrics;
import health.medunited.architecture.jaxrs.resource.Scheduler;

@ApplicationPath("/connector")
public class ConnectorApplication extends Application {
    @Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> set = new HashSet<>();
		set.add(Event.class);
		set.add(Scheduler.class);
		set.add(Metrics.class);
		return set;
	}
}
