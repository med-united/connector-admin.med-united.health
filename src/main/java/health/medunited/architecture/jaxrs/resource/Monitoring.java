package health.medunited.architecture.jaxrs.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import health.medunited.architecture.monitoring.Scheduler;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;


@Path("monitoring")
public class Monitoring {

    private static final Logger log = Logger.getLogger(Scheduler.class.getName());

    private boolean updateConnectors;
    private boolean updateCardTerminals;
    private boolean checkTIStatusOnline;


    // getters
    public boolean isUpdateConnectors() {
        return updateConnectors;
    }

    public boolean isUpdateCardTerminals() {
        return updateCardTerminals;
    }

    public boolean isCheckTIStatusOnline() {
        return checkTIStatusOnline;
    }


    //setters
    public void setUpdateConnectors(boolean updateConnectors) {
        this.updateConnectors = updateConnectors;
    }

    public void setUpdateCardTerminals(boolean updateCardTerminals) {
        this.updateCardTerminals = updateCardTerminals;
    }

    public void setCheckTIStatusOnline(boolean checkTIStatusOnline) {
        this.checkTIStatusOnline = checkTIStatusOnline;
    }


    //constructor

    public Monitoring() {

    }

    public Monitoring(boolean updateConnectors,
                      boolean updateCardTerminals,
                      boolean checkTIStatusOnline) {

        this.updateConnectors = updateConnectors;
        this.updateCardTerminals = updateCardTerminals;
        this.checkTIStatusOnline = checkTIStatusOnline;

    }

    //endpoints

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    //@Produces(MediaType.APPLICATION_JSON)
    public void updateStatus(Monitoring incomingMonitoringConfiguration) throws IOException {

        log.info("Updating the Monitoring Configuration File with the new settings");

        FileWriter writer = new FileWriter("./monitoring/MonitoringAspects.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(incomingMonitoringConfiguration,writer);
        writer.close();

    }

}

