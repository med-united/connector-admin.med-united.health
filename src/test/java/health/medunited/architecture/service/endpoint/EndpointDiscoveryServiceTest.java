package health.medunited.architecture.service.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import health.medunited.architecture.Bootstrap;
import health.medunited.architecture.entities.RuntimeConfig;
import health.medunited.architecture.service.common.security.SecretsManagerService;


class EndpointDiscoveryServiceTest {

    private static SecretsManagerService secretsManagerService;
    private RuntimeConfig runtimeConfig = Bootstrap.getRuntimeConfigKops();

    @BeforeAll
    public static void setup() {
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        RuntimeConfig runtimeConfig = Bootstrap.getRuntimeConfigKops();
        Mockito.when(httpServletRequest.getHeader("x-client-certificate")).thenReturn(runtimeConfig.getClientCertificate());
        Mockito.when(httpServletRequest.getHeader("x-client-certificate-password")).thenReturn(runtimeConfig.getClientCertificatePassword());
        secretsManagerService = new SecretsManagerService();
        secretsManagerService.setRequest(httpServletRequest);
        secretsManagerService.createSSLContext();
    }

    @Test
    @Disabled("Integration test with Kops")
    void testObtainConfiguration() throws Exception {
        EndpointDiscoveryService endpointDiscoveryService = new EndpointDiscoveryService();
        endpointDiscoveryService.secretsManagerService = secretsManagerService;
        endpointDiscoveryService.obtainConfiguration(runtimeConfig.getUrl());
        Assertions.assertFalse(endpointDiscoveryService.getEventServiceEndpointAddress().isEmpty());
    }

    @Test
    void parseSecunetExampleSDS() throws JAXBException {
        EndpointDiscoveryService edService = new EndpointDiscoveryService();
        InputStream inputStream = EndpointDiscoveryServiceTest.class.getResourceAsStream("/connectorSECUN5.53.sds");
        edService.parseInput(inputStream);
        assertEquals("5.53.0", edService.getConnectorSds().getProductInformation().getProductTypeInformation().getProductTypeVersion());
    
    }

    @Test
    void parseKocoboxExampleSDS() throws JAXBException {
        EndpointDiscoveryService edService = new EndpointDiscoveryService();
        InputStream inputStream = EndpointDiscoveryServiceTest.class.getResourceAsStream("/connectorKOCOC4.80.3.sds");
        edService.parseInput(inputStream);
        assertEquals("https://192.168.50.147:443/service/cardservice", edService.getCardServiceEndpointAddress());
    }
}
