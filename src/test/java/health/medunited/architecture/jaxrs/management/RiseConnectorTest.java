package health.medunited.architecture.jaxrs.management;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import health.medunited.architecture.model.RestartRequestBody;
import org.junit.jupiter.api.Test;

import health.medunited.architecture.jaxrs.LoggingFilter;
import health.medunited.architecture.service.common.security.SecretsManagerService;

public class RiseConnectorTest {

    String host = "https://192.168.178.75";

    private static Logger log = Logger.getLogger(RiseConnectorTest.class.getName());

    RiseConnector riseConnector = new RiseConnector();

    @Test
    public void testAcceptingAllConnections() throws Exception {
        riseConnector.secretsManagerService = new SecretsManagerService();
        riseConnector.secretsManagerService.setUpSSLContext(null);
        AbstractConnector.modifyClientBuilder = (clientBuilder) -> {
            // This will print URL and Header of request
            // clientBuilder.register(LoggingFilter.class);
            clientBuilder.connectTimeout(10, TimeUnit.SECONDS);
            clientBuilder.readTimeout(10, TimeUnit.SECONDS);
        };
        riseConnector.restart(host, "8443", new RestartRequestBody("superadmin", "Pwdpwd12"));

    }

}
