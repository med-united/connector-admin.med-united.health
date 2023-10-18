package health.medunited.architecture.jaxrs.management;

import java.io.StringReader;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
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
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import health.medunited.architecture.model.ManagementCredentials;
import health.medunited.architecture.LoggingFilter;

@ApplicationScoped
@Named("rise")
public class RiseConnector extends AbstractConnector {

    private static final Logger log = Logger.getLogger(RiseConnector.class.getName());
    private NewCookie sessionCookie;

    private static final String GET = "GET";
    private static final String POST = "POST";

    @Override
    public void restart(String connectorUrl, ManagementCredentials managementCredentials) {
        restart(connectorUrl, "8443", managementCredentials);
    }

    @Override
    public void restart(String connectorUrl, String managementPort, ManagementCredentials managementCredentials) {
        log.info("[" + connectorUrl + ":" + managementPort + "] Restarting RISE connector...");

        AbstractConnector.modifyClientBuilder = (clientBuilder) -> {
            // This will print URL and Header of request
            clientBuilder.register(LoggingFilter.class);
            clientBuilder.connectTimeout(10, TimeUnit.SECONDS);
            clientBuilder.readTimeout(10, TimeUnit.SECONDS);
        };

        Client client = buildClient();
        String cookie = getSessionCookie(client, connectorUrl, managementPort);

        Response login = connectAndRetrieveResponse(POST, "/api/v1/auth/login", client, connectorUrl, managementPort, managementCredentials);
        log.info("Login status: " + login.getStatus());
        login.close();

        Response restart = connectAndRetrieveResponse(POST, "/api/v1/mgm/reboot", client, connectorUrl, managementPort, managementCredentials);
        log.info("Restart status: " + restart.getStatus());
        restart.close();
    }

    public String getSessionCookie(Client client, String connectorUrl, String managementPort) {
        Response sessionCookieResponse = client.target(connectorUrl + ":" + managementPort)
                .path("/api/v1/users/current").request(MediaType.APPLICATION_JSON)
                .header("Referer", connectorUrl + ":" + managementPort).get();

        log.info("Status for retrieving Session Cookie: " + sessionCookieResponse.getStatus());

        String cookie = "";
        sessionCookie = new NewCookie("JSESSIONID", "");

        if (sessionCookieResponse.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
            cookie = sessionCookieResponse.getHeaderString("Set-Cookie");
            sessionCookie = new NewCookie("JSESSIONID", cookie.substring(cookie.indexOf("=") + 1, cookie.indexOf(";")));
            sessionCookieResponse.close();
        } else {
            log.warning("Could not connect. Status: " + sessionCookieResponse.getStatus());
        }
        return cookie;
    }

    Response connectAndRetrieveResponse(String httpMethod, String path, Client client, String connectorUrl, String managementPort, ManagementCredentials managementCredentials) {
        WebTarget target = client.target(connectorUrl + ":" + managementPort)
                .path(path);

        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON)
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
        if (Objects.equals(httpMethod, GET)) {
            return invocationBuilder.get();
        } else if (Objects.equals(httpMethod, POST)) {
            return invocationBuilder.post(Entity.json(new ManagementCredentialsRISE(managementCredentials.getUsername(), managementCredentials.getPassword())));
        }
        return null;
    }

    @Override
    public boolean isTIOnline(String connectorUrl, String managementPort, ManagementCredentials managementCredentials) {
        log.info("[" + connectorUrl + ":" + managementPort + "] Checking status of TI...");

        Client client = buildClient();
        String cookie = getSessionCookie(client, connectorUrl, managementPort);

        Response login = connectAndRetrieveResponse(POST, "/api/v1/auth/login", client, connectorUrl, managementPort, managementCredentials);
        log.info("Login response code: " + login.getStatus());
        login.close();

        Response tiStatusResponse = connectAndRetrieveResponse(GET, "/api/v1/status", client, connectorUrl, managementPort, managementCredentials);
        log.info("[" + connectorUrl + "] TI status response code: " + tiStatusResponse.getStatus());

        if (tiStatusResponse.getStatus() == Response.Status.OK.getStatusCode()) {
            String responseBody = tiStatusResponse.readEntity(String.class);
            tiStatusResponse.close();
            log.info(responseBody);
            JsonReader jsonReader = Json.createReader(new StringReader(responseBody));
            JsonObject jsonObject = jsonReader.readObject();
            log.info(String.valueOf(jsonObject.getJsonObject("vpnState").getBoolean("tiOnline")));
            return jsonObject.getJsonObject("vpnState").getBoolean("tiOnline");
        } else {
            log.warning("[" + connectorUrl + "] Failed to check if TI is online. Response code: " + tiStatusResponse.getStatus());
            return false;
        }
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


    // Downloads update files on Connector
    // Should be usable also for downgrades
    @Override
    public void downloadUpdate(String connectorUrl, ManagementCredentials managementCredentials, String UpdateID) {
        downloadUpdate(connectorUrl, "8500", managementCredentials, UpdateID);
    }

    @Override
    public void downloadUpdate(String connectorUrl, String managementPort, ManagementCredentials managementCredentials, String UpdateID) {
        log.log(Level.INFO, "Downloading Update On Rise connector");

    }


    // Installs downloaded update files on Connector
    @Override
    public void installUpdate(String connectorUrl, ManagementCredentials managementCredentials, String updateID, String date) {
        installUpdate(connectorUrl, "8500", managementCredentials, updateID, date);
    }

    @Override
    public void installUpdate(String connectorUrl, String managementPort, ManagementCredentials managementCredentials, String updateID, String date) {
        log.log(Level.INFO, "Installing Update On Rise connector");

    }

    //  Checks for Update
    @Override
    public Response checkUpdate(String connectorUrl, ManagementCredentials managementCredentials, String updateID, String date) {
       return checkUpdate(connectorUrl, "8500", managementCredentials);
    }

    @Override
    public Response checkUpdate(String connectorUrl, String managementPort, ManagementCredentials managementCredentials) {
        return null;
    }
}