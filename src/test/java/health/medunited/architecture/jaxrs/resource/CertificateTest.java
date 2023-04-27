package health.medunited.architecture.jaxrs.resource;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.gematik.ws.conn.certificateservicecommon.v2.CertRefEnum;
import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import health.medunited.architecture.Bootstrap;
import health.medunited.architecture.entities.RuntimeConfig;
import health.medunited.architecture.service.common.security.SecretsManagerService;

public class CertificateTest {

    private static SecretsManagerService secretsManagerService;

    ContextType contextType;

    @Disabled
    @Test
    void testGetCardHandle() throws Throwable {
        Client client = ClientBuilder.newClient();

        RuntimeConfig runtimeConfig = Bootstrap.getRuntimeConfigKops();

        String s = client.target("http://localhost:8080/frontend/connector/certificate/verifyAll").request()
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

    // @Disabled
    @Test
    void getCertificateDetailsForCardHandleAndCertType() throws Throwable {
        Client client = ClientBuilder.newClient();

        RuntimeConfig runtimeConfig = Bootstrap.getRuntimeConfigKops();

        String cardHandle = "90bee54a-0568-4fda-9389-5b3762384190";
        CertRefEnum certType = CertRefEnum.C_QES;

        String s = client
            .target("http://localhost:8080/frontend/connector/certificate")
                .path("/{certType}/{cardHandle}")
                .resolveTemplate("certType", certType.name()) 
                .resolveTemplate("cardHandle", cardHandle) 
            .request()
                .header("x-mandant-id", runtimeConfig.getMandantId())
                .header("x-client-system-id", runtimeConfig.getClientSystemId())
                .header("x-workplace-id", runtimeConfig.getWorkplaceId())
                .header("x-user-id", runtimeConfig.getUserId())
                .header("x-client-certificate", runtimeConfig.getClientCertificate())
                .header("x-client-certificate-password", runtimeConfig.getClientCertificatePassword())
                .header("x-host", runtimeConfig.getUrl())
                .accept(MediaType.APPLICATION_JSON_TYPE)
            .get(String.class);
        
        // C_AUT : [{"CN":"Test Praxis Valid"},{"SERIALNUMBER":"1-smcb-doctor-valid"},{"O":"eHealthExperts GmbH"},{"C":"DE"}]
        // C_ENC : [{"CN":"Test Praxis Valid"},{"SERIALNUMBER":"1-smcb-doctor-valid"},{"O":"eHealthExperts GmbH"},{"C":"DE"}]
        System.out.println(s);
    }
}
