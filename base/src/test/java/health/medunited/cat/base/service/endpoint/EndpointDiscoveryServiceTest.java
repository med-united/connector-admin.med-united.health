package health.medunited.cat.base.service.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import health.medunited.cat.base.Bootstrap;
import health.medunited.cat.base.entities.RuntimeConfig;
import health.medunited.cat.base.service.common.security.SecretsManagerService;


class EndpointDiscoveryServiceTest {

  EndpointDiscoveryService edService;
  private RuntimeConfig runtimeConfig;

  @BeforeEach
  void setupEach() {
    HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
    runtimeConfig = Bootstrap.getRuntimeConfigKops();
    Mockito.when(httpServletRequest.getHeader("x-client-certificate"))
        .thenReturn(runtimeConfig.getClientCertificate());
    Mockito.when(httpServletRequest.getHeader("x-client-certificate-password"))
        .thenReturn(runtimeConfig.getClientCertificatePassword());
    SecretsManagerService secretsManagerService = new SecretsManagerService();
    secretsManagerService.setRequest(httpServletRequest);
    secretsManagerService.createSSLContext();
    edService = new EndpointDiscoveryService(secretsManagerService);
  }

  @Test
  @Disabled("Integration test with Kops")
  void testObtainConfiguration() {
    edService.obtainConfiguration(runtimeConfig.getUrl(), runtimeConfig.getBasicAuthUsername(),
        runtimeConfig.getBasicAuthPassword());
    Assertions.assertFalse(edService.getEventServiceEndpointAddress().isEmpty());
  }

  @Test
  void parseSecunetExampleSDS() throws JAXBException {
    InputStream inputStream = EndpointDiscoveryServiceTest.class.getResourceAsStream(
        "/connectorSECUN5.53.0.sds");
    assertEquals("5.53.0",
        edService.parseInput(inputStream).getProductInformation().getProductTypeInformation()
            .getProductTypeVersion());
  }

  @Test
  void parseKocoboxExampleSDS() throws JAXBException {
    InputStream inputStream = EndpointDiscoveryServiceTest.class.getResourceAsStream(
        "/connectorKOCOC4.80.3.sds");
    edService.parseInput(inputStream);
    assertEquals("https://192.168.50.147:443/service/cardservice",
        edService.getCardServiceEndpointAddress());
  }
}
