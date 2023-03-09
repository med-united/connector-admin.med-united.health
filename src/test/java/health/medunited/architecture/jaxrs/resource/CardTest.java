package health.medunited.architecture.jaxrs.resource;

import de.gematik.ws.conn.cardservice.wsdl.v8.CardServicePortType;
import health.medunited.architecture.service.common.security.SecretsManagerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;


public class CardTest {

    private static SecretsManagerService secretsManagerService;

    @Inject
    CardServicePortType cardServicePortType;

    @BeforeAll
    public static void setup() {

    }

    @Test
    void testObtainConfiguration()  {
        System.out.println("------------- Card Test ----------");
    }
}
