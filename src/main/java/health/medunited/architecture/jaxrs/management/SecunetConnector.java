package health.medunited.architecture.jaxrs.management;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.*;

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

    private Response loginManagementConsole(Client client, String connectorUrl, String managementPort, ManagementCredentials managementCredentials) {
        WebTarget loginTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/rest/mgmt/ak/konten/login");

        Invocation.Builder loginBuilder = loginTarget.request(MediaType.APPLICATION_JSON);
        return loginBuilder.post(Entity.json(managementCredentials));
    }


    public void checkUpdate(String connectorUrl, String managementPort, ManagementCredentials managementCredentials) {
        log.log(Level.INFO, "Checking the Update Version of the Secunet Connector");
        System.out.println(" ");

        Client client = buildClient();

        Response loginResponse = loginManagementConsole(client, connectorUrl, managementPort, managementCredentials);


        // get current firmware Version
        WebTarget fwTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/rest/mgmt/ak/dienste/status/version");
        Invocation.Builder fwBuilder = fwTarget.request(MediaType.APPLICATION_JSON);
        fwBuilder.header("Authorization", loginResponse.getHeaders().get("Authorization").get(0));
        Response fwResponse = fwBuilder.get();

        String firmwareJson = fwResponse.readEntity(String.class);

        JSONObject jsonObject = new JSONObject(firmwareJson);
        String fwVersion = jsonObject.getString("fwVersion");
        System.out.println("Currently installed firmware version: "+fwVersion);

        //get latest possible Update version
        WebTarget updatesTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/rest/mgmt/ak/dienste/ksr/informationen/updates-konnektor");
        Invocation.Builder updatesBuilder = updatesTarget.request(MediaType.APPLICATION_JSON);
        updatesBuilder.header("Authorization", loginResponse.getHeaders().get("Authorization").get(0));
        Response updatesResponse = updatesBuilder.get();

        String updatesJson = updatesResponse.readEntity(String.class);
        int versionPosition = updatesJson.lastIndexOf("Release der Version ");

        String latestVersion = updatesJson.substring(versionPosition+20,updatesJson.indexOf('"',versionPosition+2));

        System.out.println("Latest possible update version: "+latestVersion);
        System.out.println(" ");
        if (fwVersion == latestVersion) System.out.println("Connector updated to latest version: true");
        else System.out.println("Connector updated to latest version: false");

    }
}
