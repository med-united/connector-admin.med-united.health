package health.medunited.architecture.jaxrs.management;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import health.medunited.architecture.model.ManagementCredentials;

@ApplicationScoped
@Named("secunet")
public class SecunetConnector extends AbstractConnector {

    private static final Logger log = Logger.getLogger(SecunetConnector.class.getName());

    private Response loginManagementConsole(Client client, String connectorUrl, String managementPort, ManagementCredentials managementCredentials) {
        WebTarget loginTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/rest/mgmt/ak/konten/login");

        Invocation.Builder loginBuilder = loginTarget.request(MediaType.APPLICATION_JSON);
        return loginBuilder.post(Entity.json(managementCredentials));
    }

    @Override
    public void restart(String connectorUrl, ManagementCredentials managementCredentials) {
        restart(connectorUrl, "8500", managementCredentials);
    }

    @Override
    public void restart(String connectorUrl, String managementPort, ManagementCredentials managementCredentials) {
        log.info("[" + connectorUrl + ":" + managementPort + "] Restarting Secunet connector...");

        Client client = buildClient();

        Response loginResponse = loginManagementConsole(client, connectorUrl, managementPort, managementCredentials);

        WebTarget restartTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/rest/mgmt/nk/system");
        Invocation.Builder restartBuilder = restartTarget.request(MediaType.APPLICATION_JSON);
        restartBuilder.header("Authorization", loginResponse.getHeaders().get("Authorization").get(0));
        restartBuilder.post(Entity.json(""));
    }

    @Override
    public boolean isTIOnline(String connectorUrl, String managementPort, ManagementCredentials managementCredentials) {
        log.info("[" + connectorUrl + ":" + managementPort + "] Checking status of TI...");

        Client client = buildClient();

        Response loginResponse = loginManagementConsole(client, connectorUrl, managementPort, managementCredentials);

        WebTarget checkTIStatusTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/rest/mgmt/ak/dienste/status");
        Invocation.Builder checkTIStatusBuilder = checkTIStatusTarget.request(MediaType.APPLICATION_JSON);
        checkTIStatusBuilder.header("Authorization", loginResponse.getHeaders().get("Authorization").get(0));

        Response tiStatusResponse = checkTIStatusBuilder.get();

        if (tiStatusResponse.getStatus() == Response.Status.OK.getStatusCode()) {
            String responseBody = tiStatusResponse.readEntity(String.class);
            JsonReader jsonReader = Json.createReader(new StringReader(responseBody));
            JsonObject jsonObject = jsonReader.readObject();
            return jsonObject.getBoolean("vpnTiConnected");
        } else {
            log.log(Level.WARNING, "[Secunet] Failed to check if TI is online. Response code: " + tiStatusResponse.getStatus());
            return false;
        }
    }

    void setLogToXDaysOnlyWarningAndNoPerformanceLog(String connectorUrl, String managementPort, ManagementCredentials managementCredentials, int x) {
        // q: Please tell me what a secunet konnektor is?
        // a: It's a software that is used by doctors to send data to the health insurance companies.

        // Create a client request from the following json structure:
        // and send it to the following URL: /rest/mgmt/nk/protokoll/einstellungen?strict=true
        // with the following header: Content-Type: application/json
        // with the following authorization header: see loginManagementConsole
        // and the following method: POST
        // and the following media type: MediaType.APPLICATION_JSON
        // and the following response: Response
        // and the following response status: 200
        // and the following response media type: MediaType.APPLICATION_JSON
        // and the following response entity: String
        // and the following request entity: {"logSuccessfulCryptoOps":"DISABLED","logInfo":[{"fmName":"system","severity":"DEBUG","logDays":"10","performanceLog":"ENABLED"},{"fmName":"amts","severity":"WARNING","logDays":"10","performanceLog":"DISABLED"},{"fmName":"epa","severity":"WARNING","logDays":"10","performanceLog":"DISABLED"},{"fmName":"nfdm","severity":"WARNING","logDays":"10","performanceLog":"DISABLED"},{"fmName":"vsdm","severity":"WARNING","logDays":"10","performanceLog":"DISABLED"}],"securityLogDays":"10"}
        
        Client client = buildClient();
        Response loginResponse = loginManagementConsole(client, connectorUrl, managementPort, managementCredentials);
        
        WebTarget restartTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/rest/mgmt/nk/protokoll/einstellungen").queryParam("strict", "true");
        Invocation.Builder logSetter = restartTarget.request(MediaType.APPLICATION_JSON);
        logSetter.header("Authorization", loginResponse.getHeaders().get("Authorization").get(0));

        JsonObject jsonObject = Json.createObjectBuilder().add("logSuccessfulCryptoOps", "DISABLED").add("logInfo", 
            Json.createArrayBuilder().add(
                Json.createObjectBuilder().add("fmName", "system").add("severity", "WARNING").add("logDays", x).add("performanceLog", "DISABLED").build()
            ).add(
                Json.createObjectBuilder().add("fmName", "amts").add("severity", "WARNING").add("logDays", x).add("performanceLog", "DISABLED").build()
            ).add(
                Json.createObjectBuilder().add("fmName", "epa").add("severity", "WARNING").add("logDays", x).add("performanceLog", "DISABLED").build()
            ).add(
                Json.createObjectBuilder().add("fmName", "nfdm").add("severity", "WARNING").add("logDays", x).add("performanceLog", "DISABLED").build()
            ).add(
                Json.createObjectBuilder().add("fmName", "vsdm").add("severity", "WARNING").add("logDays", x).add("performanceLog", "DISABLED").build()
            ).build()
        ).add("securityLogDays", x).build();

        Response response = logSetter.put(Entity.json(jsonObject.toString()));

        if(response.getStatusInfo().getFamily() != Status.Family.SUCCESSFUL) {
            throw new RuntimeException("Could not set log settings. Code: "+response.getStatus()+" Body: "+response.readEntity(String.class));
        }
    }



    // Downloads update files on Connector
    // Should be usable also for downgrades
    @Override
    public void downloadUpdate(String connectorUrl, ManagementCredentials managementCredentials, String updateID) {
        downloadUpdate(connectorUrl, "8500", managementCredentials, updateID);
    }

    @Override
    public void downloadUpdate(String connectorUrl, String managementPort, ManagementCredentials managementCredentials, String updateID) {
        log.info("[" + connectorUrl + ":" + managementPort + "] Downloading Update on Secunet connector...");

        Client client = buildClient();

        Response loginResponse = loginManagementConsole(client, connectorUrl, managementPort, managementCredentials);


        WebTarget downloadTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/rest/mgmt/ak/dienste/ksr/download/"+updateID);

        Invocation.Builder downloadBuilder = downloadTarget.request(MediaType.APPLICATION_JSON);
        downloadBuilder.header("Authorization", loginResponse.getHeaders().get("Authorization").get(0));
        downloadBuilder.put(Entity.json(""));
    }


    // Installs downloaded update files on Connector
    // When date is set in the past it installs the update asap
    @Override
    public void planUpdate(String connectorUrl, ManagementCredentials managementCredentials, String updateID, String date) {
        planUpdate(connectorUrl, "8500", managementCredentials, updateID, date);
    }

    @Override
    public void planUpdate(String connectorUrl, String managementPort, ManagementCredentials managementCredentials, String updateID, String date) {
        managementPort = "8500";
        log.info("[" + connectorUrl + ":" + managementPort + "] Installing Update on  Secunet connector...");

        Client client = buildClient();

        Response loginResponse = loginManagementConsole(client, connectorUrl, managementPort, managementCredentials);


        WebTarget einplanenTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/rest/mgmt/ak/dienste/ksr/einplanen");

        Invocation.Builder installBuilder = einplanenTarget.request(MediaType.APPLICATION_JSON);
        installBuilder.header("Authorization", loginResponse.getHeaders().get("Authorization").get(0));

        installBuilder.put(Entity.json("{\"nachTerminals\": null, \"updateId\": \""+updateID+"\", \"schedule\": "+date+"}"));

    }


    //  Checks for Update
    @Override
    public void checkUpdate(String connectorUrl, ManagementCredentials managementCredentials) {
       checkUpdate(connectorUrl, "8500", managementCredentials);
    }

    // In the dashboard its written: "Auf Aktualisierungen in der TI (KSR) prüfen"
    @Override
    public void checkUpdate(String connectorUrl, String managementPort, ManagementCredentials managementCredentials) {
        managementPort = "8500";
        log.info("[" + connectorUrl + ":" + managementPort + "] Check for Updates on Secunet connector...");

        Client client = buildClient();

        Response loginResponse = loginManagementConsole(client, connectorUrl, managementPort, managementCredentials);

        WebTarget einplanenTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/rest/mgmt/ak/dienste/ksr/informationen/update");

        Invocation.Builder installBuilder = einplanenTarget.request(MediaType.APPLICATION_JSON);
        installBuilder.header("Authorization", loginResponse.getHeaders().get("Authorization").get(0));

        Response updateResponse = installBuilder.put(Entity.json(""));


    }



    //  Checks for Update
    @Override
    public Response updateSettings(String connectorUrl, ManagementCredentials managementCredentials, boolean autoUpdate) {
      return updateSettings(connectorUrl, "8500", managementCredentials, autoUpdate);
    }

    // In the dashboard its written: "Auf Aktualisierungen in der TI (KSR) prüfen"
    @Override
    public Response updateSettings(String connectorUrl, String managementPort, ManagementCredentials managementCredentials, boolean autoUpdate) {
        managementPort = "8500";
        log.info("[" + connectorUrl + ":" + managementPort + "] Check for Updates on Secunet connector...");

        Client client = buildClient();

        Response loginResponse = loginManagementConsole(client, connectorUrl, managementPort, managementCredentials);

        // WebTarget einplanenTarget = client.target(connectorUrl + ":" + managementPort)
        //         .path("/rest/mgmt/ak/dienste/ksr/einstellungen?strict=true");

        WebTarget einplanenTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/rest/mgmt/ak/dienste/ksr/einstellungen")
                .queryParam("strict", "true");
        Invocation.Builder installBuilder = einplanenTarget.request(MediaType.APPLICATION_JSON);
        installBuilder.header("Authorization", loginResponse.getHeaders().get("Authorization").get(0));

        // Settings for switching Update Dowload + Install functionality on/off
        String jsonPayload = "";
        if (autoUpdate == true){
         jsonPayload = "{"
        + "\"autoUpdateCheck\": true,"
        + "\"autoUpdateDownload\": true,"
        + "\"supportTrialUpdates\": false,"
        + "\"autoActivateInventoryNetworks\": true,"
        + "\"autoUpdate\": true,"
        + "\"autoUpdateWave\": \"EARLY\","
        + "\"autoUpdateWeekDay\": \"MONDAY\","
        + "\"autoUpdateConnectorDependsOnFinishedTerminalUpdates\": false,"
        + "\"autoUpdateTimeHour\": \"01\","
        + "\"autoUpdateTimeMinute\": \"00\""
        + "}";
        }
        if (autoUpdate == false){
         jsonPayload = "{"
        + "\"autoUpdateCheck\": false,"
        + "\"autoUpdateDownload\": false,"
        + "\"supportTrialUpdates\": false,"
        + "\"autoActivateInventoryNetworks\": true,"
        + "\"autoUpdate\": false,"
        + "\"autoUpdateWave\": \"EARLY\","
        + "\"autoUpdateWeekDay\": \"MONDAY\","
        + "\"autoUpdateConnectorDependsOnFinishedTerminalUpdates\": false,"
        + "\"autoUpdateTimeHour\": \"01\","
        + "\"autoUpdateTimeMinute\": \"00\""
        + "}";
        }
        Response updateResponse = installBuilder.put(Entity.json(jsonPayload));

        String responseString = updateResponse.readEntity(String.class);
        return updateResponse;

    }




}
