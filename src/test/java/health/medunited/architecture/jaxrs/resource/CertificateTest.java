package health.medunited.architecture.jaxrs.resource;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import health.medunited.architecture.Bootstrap;
import health.medunited.architecture.entities.RuntimeConfig;
import health.medunited.architecture.service.common.security.SecretsManagerService;

@Disabled
public class CertificateTest {

    private static SecretsManagerService secretsManagerService;

    ContextType contextType;

    @Test
    void testGetCardHandle() throws Throwable {
        Client client = ClientBuilder.newClient();

        RuntimeConfig runtimeConfig = Bootstrap.getRuntimeConfig();

        String s = client.target("http://localhost:8080/connector/certificate/verifyAll").request()
            .header("X-Mandant-Id", runtimeConfig.getMandantId())
            .header("X-Client-System-Id", runtimeConfig.getClientSystemId())
            .header("X-Workplace-Id", runtimeConfig.getWorkplaceId())
            .header("X-User-Id", runtimeConfig.getUserId())
            .header("X-Client-Certificate", runtimeConfig.getClientCertificate())
            .header("X-Client-Certificate-Password", runtimeConfig.getClientCertificatePassword())
            .header("X-Host", runtimeConfig.getUrl())
            .accept(MediaType.APPLICATION_JSON_TYPE)
            .get(String.class);
        
        System.out.println(s);

    }
}
