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

import health.medunited.architecture.model.RestartRequestBody;

@ApplicationScoped
@Named("rise")
public class RiseConnector extends AbstractConnector {

    private static final Logger log = Logger.getLogger(SecunetConnector.class.getName());

    @Override
    public void restart(String connectorUrl, String managementPort, RestartRequestBody managementCredentials) {
        log.log(Level.INFO, "Restarting RISE connector");
        
        Client client = buildClient();
        Response sessionCookieResponse = client.target(connectorUrl + ":" + managementPort)
            .path("/api/v1/users/current").request(MediaType.APPLICATION_JSON)
            .header("Referer", connectorUrl + ":" + managementPort).get();

        String cookie = "";
        if(sessionCookieResponse.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
            cookie = sessionCookieResponse.getHeaderString("Set-Cookie");
            log.info("Set-Cookie: "+cookie);
        } else {
            log.warning("Could not connect status: "+sessionCookieResponse.getStatus());
        }

        Response login = loginManagementConsole(client, connectorUrl, managementPort, cookie, managementCredentials);
        
        log.info("Login status: "+login.getStatus());

        Response restart = performRestart(client, connectorUrl, managementPort, cookie, managementCredentials);

        log.info("Restart status: "+restart.getStatus());

    }

    Response loginManagementConsole(Client client, String connectorUrl, String managementPort, String cookie, RestartRequestBody managementCredentials) {
        WebTarget loginTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/api/v1/auth/login");

        Invocation.Builder loginBuilder = loginTarget.request(MediaType.APPLICATION_JSON)
        .header("Cookie", cookie)
        .header("Referer", connectorUrl + ":" + managementPort);
        return loginBuilder.post(Entity.json(managementCredentials));
    }

    Response performRestart(Client client, String connectorUrl, String managementPort, String cookie, RestartRequestBody managementCredentials) {
        WebTarget loginTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/api/v1/mgm/reboot");

        Invocation.Builder restartBuilder = loginTarget.request(MediaType.APPLICATION_JSON)
                .header("Cookie", cookie)
                .header("Referer", connectorUrl + ":" + managementPort);
        return restartBuilder.post(Entity.json(managementCredentials));

    }
}
