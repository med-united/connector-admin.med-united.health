package health.medunited.architecture.jaxrs.management;

import health.medunited.architecture.model.RestartRequestBody;
import health.medunited.architecture.service.common.security.SecretsManagerService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Named("secu_kon")
public class SecunetConnector implements Connector {

    private static final Logger log = Logger.getLogger(SecunetConnector.class.getName());

    @Inject
    SecretsManagerService secretsManagerService;

    @Override
    public void restart(String connectorUrl, String managementPort, RestartRequestBody managementCredentials) {
        log.log(Level.INFO, "Restarting Secunet connector");

        ClientBuilder clientBuilder = ClientBuilder.newBuilder();

        clientBuilder.connectTimeout(3, TimeUnit.SECONDS);
        clientBuilder.readTimeout(3, TimeUnit.SECONDS);

        SSLContext sslContext = secretsManagerService.getSslContext();

        clientBuilder.sslContext(sslContext);

        Client client = clientBuilder.build();

        Response loginResponse = loginManagementConsole(client, connectorUrl, managementPort, managementCredentials);

        WebTarget restartTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/rest/mgmt/nk/system");
        Invocation.Builder restartBuilder = restartTarget.request(MediaType.APPLICATION_JSON);
        restartBuilder.header("Authorization", loginResponse.getHeaders().get("Authorization").get(0));
        restartBuilder.post(Entity.json(""));
    }

    private Response loginManagementConsole(Client client, String connectorUrl, String managementPort, RestartRequestBody managementCredentials) {
        WebTarget loginTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/rest/mgmt/ak/konten/login");

        Invocation.Builder loginBuilder = loginTarget.request(MediaType.APPLICATION_JSON);
        return loginBuilder.post(Entity.json(managementCredentials));
    }
}
