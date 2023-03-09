package health.medunited.architecture.jaxrs.resource;

import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import health.medunited.architecture.service.common.security.SecretsManagerService;
import org.junit.jupiter.api.*;
import org.mockito.Incubating;
import org.mockito.Mockito;

import javax.inject.Inject;
import javax.naming.Context;
import javax.servlet.http.HttpServletRequest;

import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;

@Disabled
public class CertificateTest {

    private static SecretsManagerService secretsManagerService;

    ContextType contextType;

    @Inject
    EventServicePortType eventServicePortType;

    @Inject
    Certificate certificate;


    @BeforeEach
    void init() {
        contextType = new ContextType();
        contextType.setMandantId("Mandant1");
        contextType.setWorkplaceId("Workplace1");
        contextType.setClientSystemId("ClientID1");
    }

    @Test
    void testGetCardHandle() throws Throwable {
        certificate = new Certificate();
        certificate.setContextType(contextType);
        //certificate.setEventServicePortType(eventServicePortType);
        String hdl = certificate.doGetCardHandle();
        System.out.println("------------ Certificate Test -----------");
    }
}
