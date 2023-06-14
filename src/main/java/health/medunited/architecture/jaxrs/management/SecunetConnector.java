package health.medunited.architecture.jaxrs.management;

import java.io.StringReader;
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

import health.medunited.architecture.model.ManagementCredentials;

@ApplicationScoped
@Named("secunet")
public class SecunetConnector extends AbstractConnector {

    private static final Logger log = Logger.getLogger(SecunetConnector.class.getName());

    @Override
    public void restart(String connectorUrl, ManagementCredentials managementCredentials) {
        restart(connectorUrl, "8500", managementCredentials);
    }

    @Override
    public void restart(String connectorUrl, String managementPort, ManagementCredentials managementCredentials) {
        log.log(Level.INFO, "Restarting Secunet connector");

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
        log.log(Level.INFO, "[Secunet] Checking if TI is online");

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

    private Response loginManagementConsole(Client client, String connectorUrl, String managementPort, ManagementCredentials managementCredentials) {
        WebTarget loginTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/rest/mgmt/ak/konten/login");

        Invocation.Builder loginBuilder = loginTarget.request(MediaType.APPLICATION_JSON);
        return loginBuilder.post(Entity.json(managementCredentials));
    }
}
