package health.medunited.architecture.jaxrs.management;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import health.medunited.architecture.model.ManagementCredentials;
import health.medunited.architecture.LoggingFilter;

@ApplicationScoped
@Named("rise")
public class RiseConnector extends AbstractConnector {

    private static final Logger log = Logger.getLogger(SecunetConnector.class.getName());
    private NewCookie sessionCookie;

    @Override
    public void restart(String connectorUrl, ManagementCredentials managementCredentials) {
        restart(connectorUrl, "8443", managementCredentials);
    }

    @Override
    public void restart(String connectorUrl, String managementPort, ManagementCredentials managementCredentials) {
        log.log(Level.INFO, "Restarting RISE connector");

        AbstractConnector.modifyClientBuilder = (clientBuilder) -> {
            // This will print URL and Header of request
            clientBuilder.register(LoggingFilter.class);
            clientBuilder.connectTimeout(10, TimeUnit.SECONDS);
            clientBuilder.readTimeout(10, TimeUnit.SECONDS);
        };

        Client client = buildClient();
        Response sessionCookieResponse = client.target(connectorUrl + ":" + managementPort)
                .path("/api/v1/users/current").request(MediaType.APPLICATION_JSON)
                .header("Referer", connectorUrl + ":" + managementPort).get();

        String cookie;

        if (sessionCookieResponse.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
            cookie = sessionCookieResponse.getHeaderString("Set-Cookie");
            sessionCookie = new NewCookie("JSESSIONID",
                    cookie.substring(cookie.indexOf("=") + 1, cookie.indexOf(";")));
            log.log(Level.INFO, "Status for retrieving Session Cookie: " + sessionCookieResponse.getStatus());

            sessionCookieResponse.close();
            Response login = connectorCommunication("/api/v1/auth/login", client, connectorUrl, managementPort, cookie, managementCredentials);
            log.info("Login status: " + login.getStatus());

            login.close();

            Response restart = connectorCommunication("/api/v1/mgm/reboot", client, connectorUrl, managementPort, cookie, managementCredentials);
            log.info("Restart status: " + restart.getStatus());
            restart.close();
        } else {
            log.warning("Unable to restart connector");
            log.warning("Connection attempt to " + connectorUrl + ":" + managementPort + " failed");
            log.warning("Could not connect. Status: " + sessionCookieResponse.getStatus());
        }

    }

    Response connectorCommunication(String path, Client client, String connectorUrl, String managementPort, String cookie, ManagementCredentials managementCredentials) {
        WebTarget loginTarget = client.target(connectorUrl + ":" + managementPort)
                .path(path);

        Invocation.Builder loginBuilder = loginTarget.request(MediaType.APPLICATION_JSON)
                .cookie(sessionCookie)
                .header("Accept", "*/*")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Connection", "keep-alive")
                .header("Pragma", "no-cache")
                .header("sec-ch-ua", "'Microsoft Edge';v='113', 'Chromium';v='113', 'Not-A.Brand';v='24'")
                .header("sec-ch-ua-platform", "Windows")
                .header("sec-ch-ua-mobile", "?0")
                .header("Cache-Control", "no-cache")
                .header("Accept", "application/json, text/plain, */*")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.1774.42")
                .header("Content-Type", "application/json")
                .header("X-Requested-With", "RISEHttpRequest")
                .header("If-Modified-Since", "Thu, 01 Jan 1970 00:00:00 GMT")
                .header("Referer", connectorUrl + ":" + managementPort);
        return loginBuilder.post(Entity.json(new ManagementCredentialsRISE(managementCredentials.getUsername(), managementCredentials.getPassword())));

    }


    public static class ManagementCredentialsRISE {
        String user;
        String password;

        public ManagementCredentialsRISE() {

        }

        public ManagementCredentialsRISE(String user, String password) {
            this.user = user;
            this.password = password;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }
    }

    @Override
    public String availableUpdate(String connectorUrl, ManagementCredentials managementCredentials){
        return availableUpdate(connectorUrl, "8443", managementCredentials);

    }

    @Override
    public String availableUpdate(String connectorUrl, String managementPort, ManagementCredentials managementCredentials){
        return "";
    }

}
