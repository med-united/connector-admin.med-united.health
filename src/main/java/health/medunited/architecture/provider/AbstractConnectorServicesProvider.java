package health.medunited.architecture.provider;

import health.medunited.architecture.service.endpoint.SSLUtilities;
import health.medunited.architecture.z.SecretsManagerService;
import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.xml.ws.BindingProvider;
import java.util.logging.Logger;

public abstract class AbstractConnectorServicesProvider {

    private static final Logger log = Logger.getLogger(AbstractConnectorServicesProvider.class.getName());

    @Inject
    SecretsManagerService secretsManagerService;

    private de.gematik.ws.conn.vsds.vsdservice.v5.VSDServicePortType vSDServicePortType;
    private de.gematik.ws.conn.cardservice.wsdl.v8.CardServicePortType cardServicePortType;
    private de.gematik.ws.conn.certificateservice.wsdl.v6.CertificateServicePortType certificateService;
    private de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType eventServicePortType;
    private de.gematik.ws.conn.authsignatureservice.wsdl.v7.AuthSignatureServicePortType authSignatureServicePortType;
    private de.gematik.ws.conn.signatureservice.wsdl.v7.SignatureServicePortTypeV740 signatureServicePortType;
    private de.gematik.ws.conn.signatureservice.wsdl.v7.SignatureServicePortTypeV755 signatureServicePortTypeV755;
    private de.gematik.ws.conn.connectorcontext.v2.ContextType contextType;

    public void initializeServices() {
        initializeServices(false);
    }

    private void initializeServices(boolean throwEndpointException) {
        //endpointDiscoveryService.obtainConfiguration(throwEndpointException);
        initializeEventServicePortType();
    }

    private void initializeEventServicePortType() {
        de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType service = new de.gematik.ws.conn.eventservice.wsdl.v7.EventService(getClass().getResource("/EventService.wsdl"))
                .getEventServicePort();

        BindingProvider bp = (BindingProvider) service;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "https://192.168.178.42:443/ws/EventService");
//        if(endpointDiscoveryService.getEventServiceEndpointAddress() != null) {
//            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
//                    endpointDiscoveryService.getEventServiceEndpointAddress());
//        } else {
//            System.out.println("EventServiceEndpointAddress is null");
//        }
        configureBindingProvider(bp);

        eventServicePortType = service;
    }

    private void configureBindingProvider(BindingProvider bindingProvider) {
        SSLContext sslContext = secretsManagerService.getSslContext();
        if (sslContext != null) {
            bindingProvider.getRequestContext().put("com.sun.xml.ws.transport.https.client.SSLSocketFactory",
                    sslContext.getSocketFactory());
        }
        bindingProvider.getRequestContext().put("com.sun.xml.ws.transport.https.client.hostname.verifier",
                new SSLUtilities.FakeHostnameVerifier());
    }

    public de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType getEventServicePortType() {
        return this.eventServicePortType;
    }

}
