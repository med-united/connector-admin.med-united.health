package health.medunited.cat.base.jaxrs.resource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/scheduler")
public class Scheduler {

  @Inject
  health.medunited.cat.base.monitoring.Scheduler scheduler;

  @GET
  @Path("/monitorConnectors")
  public void monitorConnectors() {
    scheduler.monitorConnectors();
  }
}
