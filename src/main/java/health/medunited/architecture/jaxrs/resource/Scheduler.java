package health.medunited.architecture.jaxrs.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/scheduler")
public class Scheduler {

    @Inject
    health.medunited.architecture.monitoring.Scheduler scheduler;

    @GET
    @Path("/monitorConnectors")
    public void monitorConnectors() {
        // scheduler.monitorConnectors();
    }
}
