package health.medunited.architecture.jaxrs.resource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/scheduler")
public class Scheduler {

    @Inject
    health.medunited.architecture.monitoring.Scheduler scheduler;

    @GET
    @Path("/monitorConnectors")
    public void monitorConnectors() {
            scheduler.monitorConnectors();
    }
}
