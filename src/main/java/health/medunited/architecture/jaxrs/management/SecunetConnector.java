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

        Client client = buildClient();

        Response loginResponse = loginManagementConsole(client, connectorUrl, managementPort, managementCredentials);

        WebTarget updateTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/management/home/system/aktualisierung/konnektor");

        Invocation.Builder updateBuilder = updateTarget.request(MediaType.APPLICATION_JSON);
        Response updateResponse = updateBuilder.post(Entity.json(managementCredentials));

        Object log2 = updateResponse.getHeaders();

        System.out.println(log2);
    }
}
