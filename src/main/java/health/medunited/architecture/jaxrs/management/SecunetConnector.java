package health.medunited.architecture.jaxrs.management;

import java.io.StringReader;
import java.sql.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import health.medunited.architecture.jaxrs.resource.KonnektorService;
import health.medunited.architecture.jaxrs.resource.UpdateManagementService;
import health.medunited.architecture.model.ManagementCredentials;

@ApplicationScoped
@Named("secunet")
public class SecunetConnector extends AbstractConnector {

        @Inject
        private UpdateManagementService updateManagementService;

        @Inject
        private KonnektorService konnektorService;

        @PersistenceContext(unitName = "dashboard")
        private EntityManager em;

        private static final Logger log = Logger.getLogger(SecunetConnector.class.getName());

        private Response loginManagementConsole(Client client, String connectorUrl, String managementPort,
                        ManagementCredentials managementCredentials) {
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
                        log.log(Level.WARNING,
                                        "[Secunet] Failed to check if TI is online. Response code: " + tiStatusResponse.getStatus());
            return false; 
                }
        }

        void setLogToXDaysOnlyWarningAndNoPerformanceLog(String connectorUrl, String managementPort,
                        ManagementCredentials managementCredentials, int x) {
                // q: Please tell me what a secunet konnektor is?
                // a: It's a software that is used by doctors to send data to the health
                // insurance companies.

                // Create a client request from the following json structure:
                // and send it to the following URL:
                // /rest/mgmt/nk/protokoll/einstellungen?strict=true
                // with the following header: Content-Type: application/json
                // with the following authorization header: see loginManagementConsole
                // and the following method: POST
                // and the following media type: MediaType.APPLICATION_JSON
                // and the following response: Response
                // and the following response status: 200
                // and the following response media type: MediaType.APPLICATION_JSON
                // and the following response entity: String
                // and the following request entity:
                // {"logSuccessfulCryptoOps":"DISABLED","logInfo":[{"fmName":"system","severity":"DEBUG","logDays":"10","performanceLog":"ENABLED"},{"fmName":"amts","severity":"WARNING","logDays":"10","performanceLog":"DISABLED"},{"fmName":"epa","severity":"WARNING","logDays":"10","performanceLog":"DISABLED"},{"fmName":"nfdm","severity":"WARNING","logDays":"10","performanceLog":"DISABLED"},{"fmName":"vsdm","severity":"WARNING","logDays":"10","performanceLog":"DISABLED"}],"securityLogDays":"10"}

                Client client = buildClient();
                Response loginResponse = loginManagementConsole(client, connectorUrl, managementPort, managementCredentials);
 
                WebTarget restartTarget = client.target(connectorUrl + ":" + managementPort)
                                .path("/rest/mgmt/nk/protokoll/einstellungen").queryParam("strict", "true");
                Invocation.Builder logSetter = restartTarget.request(MediaType.APPLICATION_JSON);
                logSetter.header("Authorization", loginResponse.getHeaders().get("Authorization").get(0));

                JsonObject jsonObject = Json.createObjectBuilder().add("logSuccessfulCryptoOps", "DISABLED").add("logInfo",
                                                Json.createArrayBuilder().add(
                                                                Json.createObjectBuilder().add("fmName", "system").add("severity", "WARNING").add("logDays", x)
                                                                                                .add("performanceLog", "DISABLED").build())
                                                                .add(
                                                                                Json.createObjectBuilder().add("fmName", "amts").add("severity", "WARNING")
                                                                                                .add("logDays", x).add("performanceLog", "DISABLED").build())
                                                                .add(
                                                                                Json.createObjectBuilder().add("fmName", "epa").add("severity", "WARNING")
                                                                                                .add("logDays", x).add("performanceLog", "DISABLED").build())
                                                                .add(
                                                                                Json.createObjectBuilder().add("fmName", "nfdm").add("severity", "WARNING")
                                        .add("logDays", x).add("performanceLog", "DISABLED").build())
                        .add(
                                Json.createObjectBuilder().add("fmName", "vsdm").add("severity", "WARNING")
                                                                                                .add("logDays", x).add("performanceLog", "DISABLED").build())
                                                                .build())
                                .add("securityLogDays", x).build();

                Response response = logSetter.put(Entity.json(jsonObject.toString()));

                if (response.getStatusInfo().getFamily() != Status.Family.SUCCESSFUL) {
                        throw new RuntimeException("Could not set log settings. Code: " + response.getStatus() + " Body: "
                                                        + response.readEntity(String.class));
                }
        }

        // Downloads update files on Connector
        // Should be usable also for downgrades
        @Override
        public void downloadUpdate(String connectorUrl, ManagementCredentials managementCredentials, String updateID) {
                downloadUpdate(connectorUrl, "8500", managementCredentials, updateID);
        }

        @Override
        public void downloadUpdate(String connectorUrl, String managementPort, ManagementCredentials managementCredentials,
                        String updateID) {

                Client client = buildClient();

                Response loginResponse = loginManagementConsole(client, connectorUrl, managementPort,
                                managementCredentials);

                WebTarget downloadTarget = client.target(connectorUrl + ":" + managementPort)
                                .path("/rest/mgmt/ak/dienste/ksr/download/" + updateID);

                Invocation.Builder downloadBuilder = downloadTarget.request(MediaType.APPLICATION_JSON);
                downloadBuilder.header("Authorization", loginResponse.getHeaders().get("Authorization").get(0));
                downloadBuilder.put(Entity.json(""));
        }

        // Installs downloaded update files on Connector
        // When date is set in the past it installs the update asap
        @Override
        public void planUpdate(String connectorUrl, ManagementCredentials managementCredentials, String updateID,
                        String date) {
                planUpdate(connectorUrl, "8500", managementCredentials, updateID, date);
        }

        @Override
        public void planUpdate(String connectorUrl, String managementPort, ManagementCredentials managementCredentials,
                        String updateID, String date) {
                managementPort = "8500";
                log.info("[" + connectorUrl + ":" + managementPort + "] Installing Update on  Secunet connector...");

                Client client = buildClient();

                Response loginResponse = loginManagementConsole(client, connectorUrl, managementPort,
                                managementCredentials);

                WebTarget einplanenTarget = client.target(connectorUrl + ":" + managementPort)
                                .path("/rest/mgmt/ak/dienste/ksr/einplanen");

                Invocation.Builder installBuilder = einplanenTarget.request(MediaType.APPLICATION_JSON);
                installBuilder.header("Authorization", loginResponse.getHeaders().get("Authorization").get(0));

                installBuilder.put(Entity
                                .json("{\"nachTerminals\": null, \"updateId\": \"" + updateID + "\", \"schedule\": "
                                                + date + "}"));

        }

        // Checks for Update
        @Override
        public void checkUpdate(String connectorUrl, ManagementCredentials managementCredentials) {
                checkUpdate(connectorUrl, "8500", managementCredentials);
        }

        // In the dashboard its written: "Auf Aktualisierungen in der TI (KSR) prüfen"
        @Override
        public void checkUpdate(String connectorUrl, String managementPort,
                        ManagementCredentials managementCredentials) {
                managementPort = "8500";
                log.info("[" + connectorUrl + ":" + managementPort + "] Check for Updates on Secunet connector...");

                Client client = buildClient();

                Response loginResponse = loginManagementConsole(client, connectorUrl, managementPort,
                                managementCredentials);

                WebTarget einplanenTarget = client.target(connectorUrl + ":" + managementPort)
                                .path("/rest/mgmt/ak/dienste/ksr/informationen/update");

                Invocation.Builder installBuilder = einplanenTarget.request(MediaType.APPLICATION_JSON);
                installBuilder.header("Authorization", loginResponse.getHeaders().get("Authorization").get(0));

                // Response updateResponse = installBuilder.put(Entity.json(""));
                installBuilder.put(Entity.json(""));

        }

        // Get Release Infos
        @Override
        public Response getReleaseInfo(String connectorUrl, ManagementCredentials managementCredentials) {
                return getReleaseInfo(connectorUrl, "8500", managementCredentials);
        }

        @Override
        public Response getReleaseInfo(String connectorUrl, String managementPort,
                        ManagementCredentials managementCredentials) {

                managementPort = "8500";
                log.info("[" + connectorUrl + ":" + managementPort + "] Check for Updates on     Secunet connector...");

                Client client = buildClient();

                Response loginResponse = loginManagementConsole(client, connectorUrl,
                                managementPort,
                                managementCredentials);

                WebTarget releaseInfoTarget = client.target(connectorUrl + ":" +
                                managementPort)
                                .path("/rest/mgmt/ak/dienste/status/releaseinfos");

                Invocation.Builder installBuilder = releaseInfoTarget.request(MediaType.APPLICATION_JSON);
                installBuilder.header("Authorization",
                                loginResponse.getHeaders().get("Authorization").get(0));

                Response updateResponse = installBuilder.get();
                String responseString = updateResponse.readEntity(String.class); // Convert response to String

                // Encapsulate the string in a Response object
                // Response response = Response.status(updateResponse.getStatus())
                // .entity(responseString)
                // .build();
                return updateResponse;
        }

        // Set Settings for AutoUpdate On/Off
        @Override
        public Response updateSettings(String connectorUrl, ManagementCredentials managementCredentials,
                        boolean autoUpdate) {
                return updateSettings(connectorUrl, "8500", managementCredentials, autoUpdate);
        }

        @Override
        public Response updateSettings(String connectorUrl, String managementPort,
                        ManagementCredentials managementCredentials, boolean autoUpdate) {
                managementPort = "8500";
                log.info("[" + connectorUrl + ":" + managementPort + "] Check for Updates on Secunet connector...");

                Client client = buildClient();

                Response loginResponse = loginManagementConsole(client, connectorUrl, managementPort,
                                managementCredentials);

                WebTarget einplanenTarget = client.target(connectorUrl + ":" + managementPort)
                                .path("/rest/mgmt/ak/dienste/ksr/einstellungen")
                                .queryParam("strict", "true");
                Invocation.Builder installBuilder = einplanenTarget.request(MediaType.APPLICATION_JSON);
                installBuilder.header("Authorization", loginResponse.getHeaders().get("Authorization").get(0));

                boolean autoUpdateCheck_val;
                boolean autoUpdateDownload_val;
                boolean supportTrialUpdates_val;
                boolean autoActivateInventoryNetworks_val;
                boolean autoUpdate_val;
                String autoUpdateWave_val;
                String autoUpdateWeekDay_val;
                boolean autoUpdateConnectorDependsOnFinishedTerminalUpdates_val;
                String autoUpdateTimeHour_val;
                String autoUpdateTimeMinute_val;

                if (autoUpdate) {
                        autoUpdateCheck_val = true;
                        autoUpdateDownload_val = true;
                        supportTrialUpdates_val = false;
                        autoActivateInventoryNetworks_val = true;
                        autoUpdate_val = true;
                        autoUpdateWave_val = "EARLY";
                        autoUpdateWeekDay_val = "MONDAY";
                        autoUpdateConnectorDependsOnFinishedTerminalUpdates_val = false;
                        autoUpdateTimeHour_val = "01";
                        autoUpdateTimeMinute_val = "00";
                } else {
                        autoUpdateCheck_val = false;
                        autoUpdateDownload_val = false;
                        supportTrialUpdates_val = false;
                        autoActivateInventoryNetworks_val = true;
                        autoUpdate_val = false;
                        autoUpdateWave_val = "EARLY";
                        autoUpdateWeekDay_val = "MONDAY";
                        autoUpdateConnectorDependsOnFinishedTerminalUpdates_val = false;
                        autoUpdateTimeHour_val = "01";
                        autoUpdateTimeMinute_val = "00";
                }

                String jsonPayload = "{"
                                + "\"autoUpdateCheck\": " + autoUpdateCheck_val + ","
                                + "\"autoUpdateDownload\": " + autoUpdateDownload_val + ","
                                + "\"supportTrialUpdates\": " + supportTrialUpdates_val + ","
                                + "\"autoActivateInventoryNetworks\": " + autoActivateInventoryNetworks_val + ","
                                + "\"autoUpdate\": " + autoUpdate_val + ","
                                + "\"autoUpdateWave\": \"" + autoUpdateWave_val + "\","
                                + "\"autoUpdateWeekDay\": \"" + autoUpdateWeekDay_val + "\","
                                + "\"autoUpdateConnectorDependsOnFinishedTerminalUpdates\": "
                                + autoUpdateConnectorDependsOnFinishedTerminalUpdates_val + ","
                                + "\"autoUpdateTimeHour\": \"" + autoUpdateTimeHour_val + "\","
                                + "\"autoUpdateTimeMinute\": \"" + autoUpdateTimeMinute_val + "\""
                                + "}";

                updateManagementService.createUpdateStatus(
                                "TBD-1.0",
                                true,
                                new Date(2023, 10, 3), // TODO: other date format
                                autoUpdate,
                                connectorUrl,
                                "TBD-runtimeConfig",
                                autoUpdateCheck_val,
                                autoUpdateDownload_val,
                                supportTrialUpdates_val,
                                autoActivateInventoryNetworks_val,
                                autoUpdate_val,
                                autoUpdateWave_val,
                                autoUpdateWeekDay_val,
                                autoUpdateConnectorDependsOnFinishedTerminalUpdates_val,
                                autoUpdateTimeHour_val,
                                autoUpdateTimeMinute_val);

                Random random = new Random();
                konnektorService.create(
                                "TBD-runtimeConfig",
                                connectorUrl,
                                "TBD-1.0",
                                random.nextBoolean(),
                                autoUpdate,
                                random.nextBoolean(),
                                random.nextInt(4) + 3,
                                "");
                Response updateResponse = installBuilder.put(Entity.json(jsonPayload));

                return updateResponse;

        }
}
