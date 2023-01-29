package health.medunited.architecture.provider;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.BindingProvider;

import health.medunited.architecture.service.common.security.SecretsManagerService;
import health.medunited.architecture.service.endpoint.SSLUtilities;

@RequestScoped
public class ConnectorServicesProducer {

    private static final Logger log = Logger.getLogger(ConnectorServicesProducer.class.getName());

    @Inject
    SecretsManagerService secretsManagerService;

    @Inject
    HttpServletRequest httpServletRequest;

    static {
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dumpTreshold", "999999");
    }

    private de.gematik.ws.conn.vsds.vsdservice.v5.VSDServicePortType vSDServicePortType;
    private de.gematik.ws.conn.cardservice.wsdl.v8.CardServicePortType cardServicePortType;
    private de.gematik.ws.conn.certificateservice.wsdl.v6.CertificateServicePortType certificateService;
    private de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType eventServicePortType;
    private de.gematik.ws.conn.authsignatureservice.wsdl.v7.AuthSignatureServicePortType authSignatureServicePortType;
    private de.gematik.ws.conn.signatureservice.wsdl.v7.SignatureServicePortTypeV740 signatureServicePortType;
    private de.gematik.ws.conn.signatureservice.wsdl.v7.SignatureServicePortTypeV755 signatureServicePortTypeV755;
    private de.gematik.ws.conn.connectorcontext.v2.ContextType contextType;

    public void setSecretsManagerService(SecretsManagerService secretsManagerService) {
        this.secretsManagerService = secretsManagerService;
    }

    @PostConstruct
    public void initializeServices() {
        initializeServices(false);
    }

    private void initializeServices(boolean throwEndpointException) {
        //endpointDiscoveryService.obtainConfiguration(throwEndpointException);
        initializeEventServicePortType(httpServletRequest.getHeader("x-host"));
    }

    public void initializeEventServicePortType(String host) {
        de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType service =
                new de.gematik.ws.conn.eventservice.wsdl.v7.EventService(
                        getClass().getResource("/EventService.wsdl")).getEventServicePort();

        BindingProvider bp = (BindingProvider) service;
        String connectorUrl = host;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "https://" + connectorUrl + ":443/ws/EventService");
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

    @Produces
    public de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType getEventServicePortType() {
        return this.eventServicePortType;
    }

}
