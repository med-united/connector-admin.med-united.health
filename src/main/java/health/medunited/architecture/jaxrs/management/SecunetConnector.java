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


    @Override
    public String availableUpdate(String connectorUrl, ManagementCredentials managementCredentials){
       return availableUpdate(connectorUrl, "8500",  managementCredentials);
    }

    @Override
    public String availableUpdate(String connectorUrl, String managementPort,  ManagementCredentials managementCredentials){
        log.log(Level.INFO, "Updating Secunet connector");

        Client client = buildClient();

        Response loginResponse = loginManagementConsole(client, connectorUrl, managementPort, managementCredentials);
        String productCode = managementCredentials.getProductCode();
        String productVendorID = managementCredentials.getProductVendorID();
        String HWVersion = managementCredentials.getHWVersion();
        String FWVersion = managementCredentials.getFWVersion();
        Response updateResponse = client.target(connectorUrl + ":" + managementPort)
                .path("/rest/mgmt/ak/dienste/ksr/informationen/updates-terminal/" + productVendorID + "/" + productCode + "/" + HWVersion + "/" + FWVersion)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", loginResponse.getHeaders().get("Authorization").get(0)).get();
        return updateResponse.readEntity(String.class);
    }
}
