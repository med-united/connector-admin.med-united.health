package health.medunited.architecture.service.endpoint;

import health.medunited.architecture.service.common.security.SecretsManagerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Disabled("Tests require a connector/simulator setup")
class EndpointDiscoveryServiceTest {

    private static SecretsManagerService secretsManagerService;

    @BeforeAll
    public static void setup() {
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getHeader("x-client-certificate")).thenReturn("data:application/x-pkcs12;base64,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");
        Mockito.when(httpServletRequest.getHeader("x-client-certificate-password")).thenReturn("123456");
        secretsManagerService = new SecretsManagerService();
        secretsManagerService.setRequest(httpServletRequest);
        secretsManagerService.createSSLContext();
    }

    @Test
    void testObtainConfiguration() throws IOException, ParserConfigurationException {
        EndpointDiscoveryService endpointDiscoveryService = new EndpointDiscoveryService();
        endpointDiscoveryService.secretsManagerService = secretsManagerService;
        //TODO fix this in order to make it work
        endpointDiscoveryService.obtainConfiguration("https://127.0.0.1");
        Assertions.assertFalse(endpointDiscoveryService.getEventServiceEndpointAddress().isEmpty());
    }
}
