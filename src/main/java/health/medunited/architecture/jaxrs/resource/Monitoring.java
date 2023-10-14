package health.medunited.architecture.jaxrs.resource;

import health.medunited.architecture.monitoring.Scheduler;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;


@Path("monitoring")
public class Monitoring {

    private static final Logger log = Logger.getLogger(Scheduler.class.getName());
  
    private FileReader reader;


    //endpoints

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    //@Produces(MediaType.APPLICATION_JSON)
    public void updateStatus(MonitoringRequest incomingMonitoringConfiguration) throws IOException {

        log.info("Updating the Monitoring Configuration File with the new settings");

        JsonbConfig config = new JsonbConfig().withFormatting(true);
        Jsonb jsonb = JsonbBuilder.newBuilder().withConfig(config).build();

        String serialized = jsonb.toJson(incomingMonitoringConfiguration);
        FileWriter writer = new FileWriter("./monitoring/MonitoringAspects.json");
        writer.write(serialized);
        writer.close();

    }

    @GET
    @Path("monitoringRequest")
    @Produces(MediaType.APPLICATION_JSON)
    public MonitoringRequest serveMonitoringAspects() {
        Jsonb jsonb = JsonbBuilder.create();

        try {
            reader = new FileReader("./monitoring/MonitoringAspects.json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        MonitoringRequest incomingMonitoring = jsonb.fromJson(reader, MonitoringRequest.class);

        return incomingMonitoring;
    }

}

