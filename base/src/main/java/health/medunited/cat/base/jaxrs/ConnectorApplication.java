package health.medunited.cat.base.jaxrs;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import health.medunited.cat.base.jaxrs.resource.Card;
import health.medunited.cat.base.jaxrs.resource.Certificate;
import health.medunited.cat.base.jaxrs.resource.ConnectorManagement;
import health.medunited.cat.base.jaxrs.resource.ConnectorSds;
import health.medunited.cat.base.jaxrs.resource.Event;
import health.medunited.cat.base.jaxrs.resource.Metrics;
import health.medunited.cat.base.jaxrs.resource.Scheduler;

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
    return set;
  }
}
