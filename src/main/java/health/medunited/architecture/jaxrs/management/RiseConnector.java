package health.medunited.architecture.jaxrs.management;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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

    }

    Response loginManagementConsole(Client client, String connectorUrl, String managementPort, String cookie, RestartRequestBody managementCredentials) {
        WebTarget loginTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/api/v1/auth/login");

        Invocation.Builder loginBuilder = loginTarget.request(MediaType.APPLICATION_JSON)
        .header("Cookie", cookie)
        .header("Referer", connectorUrl + ":" + managementPort);
        return loginBuilder.post(Entity.json(managementCredentials));
    }
}
