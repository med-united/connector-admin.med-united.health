
package health.medunited.architecture.jaxrs.management;

import health.medunited.architecture.model.ManagementCredentials;
import health.medunited.architecture.service.common.security.SecretsManagerService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import java.util.logging.Logger;

@Disabled
public class SecunetConnectorTest {

    String host = "https://192.168.178.42";
    String username = ""; //specify before the test
    String password = ""; //specify before the test

    private static Logger log = Logger.getLogger(SecunetConnectorTest.class.getName());

    SecunetConnector secunetConnector = new SecunetConnector();

    @Test
    @Disabled
    public void testRestart() throws Exception {
        secunetConnector.secretsManagerService = new SecretsManagerService();
        secunetConnector.secretsManagerService.setUpSSLContext(null);
        AbstractConnector.modifyClientBuilder = (clientBuilder) -> {
            // This will print URL and Header of request
            // clientBuilder.register(LoggingFilter.class);
            clientBuilder.connectTimeout(10, TimeUnit.SECONDS);
            clientBuilder.readTimeout(10, TimeUnit.SECONDS);
        };
        secunetConnector.restart(host, new ManagementCredentials(username, password));

    }

    @Test
    @Disabled
    public void testCheckUpdate() throws Exception {

        secunetConnector.secretsManagerService = new SecretsManagerService();
        secunetConnector.secretsManagerService.setUpSSLContext(null);
        AbstractConnector.modifyClientBuilder = (clientBuilder) -> {
            // This will print URL and Header of request
            // clientBuilder.register(LoggingFilter.class);
            clientBuilder.connectTimeout(10, TimeUnit.SECONDS);
            clientBuilder.readTimeout(10, TimeUnit.SECONDS);
        };
        secunetConnector.checkUpdate(host, new ManagementCredentials(username, password));

    }
}
