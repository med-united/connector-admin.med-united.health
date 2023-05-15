package health.medunited.architecture.jaxrs.resource;

import health.medunited.architecture.Bootstrap;
import health.medunited.architecture.entities.RuntimeConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

@Disabled
class CardTest {

    @Test
    void testAllPinStatus() {
        Client client = ClientBuilder.newClient();

        RuntimeConfig runtimeConfig = Bootstrap.getRuntimeConfigKops();

        String s = client.target("http://localhost:8080/connector/card/pinStatus").request()
                .header("X-Mandant-Id", runtimeConfig.getMandantId())
                .header("X-Client-System-Id", runtimeConfig.getClientSystemId())
                .header("X-Workplace-Id", runtimeConfig.getWorkplaceId())
                .header("X-User-Id", runtimeConfig.getUserId())
                .header("X-Use-SSL", runtimeConfig.getUseSSL())
                .header("X-Client-Certificate", runtimeConfig.getClientCertificate())
                .header("X-Client-Certificate-Password", runtimeConfig.getClientCertificatePassword())
                .header("X-Host", runtimeConfig.getUrl())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(String.class);

        System.out.println(s);
    }

}