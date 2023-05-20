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
import javax.ws.rs.core.NewCookie;
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

        String responseString = login.readEntity(String.class);
        log.info("resp"+responseString);

        log.info("Login status: "+login.getStatus());
        login.close();


       // Response restart = performRestart(client, connectorUrl, managementPort, cookie, managementCredentials);

        //log.info("Restart status: "+restart.getStatus());

    }

    Response loginManagementConsole(Client client, String connectorUrl, String managementPort, String cookie, RestartRequestBody managementCredentials) {
        WebTarget loginTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/api/v1/auth/login");

        String cookieSubstr = cookie.substring(cookie.indexOf("=")+1,cookie.indexOf(";"));

        NewCookie sessionCookie = new NewCookie("JSESSIONID", cookieSubstr);

        Invocation.Builder loginBuilder = loginTarget.request(MediaType.APPLICATION_JSON)
                .cookie(sessionCookie)
                .header("User-Agent", "PostmanRuntime/7.32.2")
                .header("Accept", "*/*")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Connection", "keep-alive")
                //.header("Cookie", cookieSubstr)
                //.header("Host", connectorUrl + ":" + managementPort)
                //.header("Sec-Fetch-Mode", "cors")
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
