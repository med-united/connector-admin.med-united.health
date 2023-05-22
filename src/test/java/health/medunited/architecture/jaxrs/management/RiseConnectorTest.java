package health.medunited.architecture.jaxrs.management;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import health.medunited.architecture.model.ManagementCredentials;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import health.medunited.architecture.service.common.security.SecretsManagerService;

@Disabled
public class RiseConnectorTest {

    String host = "https://192.168.178.75";
    String user = ""; //specify before the test
    String password = ""; //specify before the test

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
        riseConnector.restart(host, new ManagementCredentials(user,password));

    }

}
