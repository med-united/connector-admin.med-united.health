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


    private boolean aktualisierung_konnektor;
    private boolean aktualisierung_kartenterminals;
    private boolean ueberpruefung_onlinestatusti;


    // getters
    public boolean isAktualisierung_konnektor() {
        return aktualisierung_konnektor;
    }

    public boolean isAktualisierung_kartenterminals() {
        return aktualisierung_kartenterminals;
    }

    public boolean isUeberpruefung_onlinestatusti() {
        return ueberpruefung_onlinestatusti;
    }


    //setters
    public void setAktualisierung_konnektor(boolean aktualisierung_konnektor) {
        this.aktualisierung_konnektor = aktualisierung_konnektor;
    }

    public void setAktualisierung_kartenterminals(boolean aktualisierung_kartenterminals) {
        this.aktualisierung_kartenterminals = aktualisierung_kartenterminals;
    }

    public void setUeberpruefung_onlinestatusti(boolean ueberpruefung_onlinestatusti) {
        this.ueberpruefung_onlinestatusti = ueberpruefung_onlinestatusti;
    }


    //constructor

    public Monitoring() {

    }

    public Monitoring(boolean aktualisierung_konnektor,
                      boolean aktualisierung_kartenterminals,
                      boolean ueberpruefung_onlinestatusti) {

        this.aktualisierung_konnektor = aktualisierung_konnektor;
        this.aktualisierung_kartenterminals = aktualisierung_kartenterminals;
        this.ueberpruefung_onlinestatusti = ueberpruefung_onlinestatusti;

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

