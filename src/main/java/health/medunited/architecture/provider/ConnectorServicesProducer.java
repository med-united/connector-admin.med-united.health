package health.medunited.architecture.provider;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.BindingProvider;

import health.medunited.architecture.service.common.security.SecretsManagerService;
import health.medunited.architecture.service.endpoint.EndpointDiscoveryService;
import health.medunited.architecture.service.endpoint.SSLUtilities;

@RequestScoped
public class ConnectorServicesProducer {

    private static final Logger log = Logger.getLogger(ConnectorServicesProducer.class.getName());

    @Inject
    SecretsManagerService secretsManagerService;

    @Inject
    HttpServletRequest httpServletRequest;

    @Inject
    EndpointDiscoveryService endpointDiscoveryService;

    static {
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dumpTreshold", "999999");
    }

    private de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType eventServicePortType;
    private de.gematik.ws.conn.cardservice.wsdl.v8.CardServicePortType cardServicePortType;
    private de.gematik.ws.conn.certificateservice.wsdl.v6.CertificateServicePortType certificateServicePortType;

    public void setSecretsManagerService(SecretsManagerService secretsManagerService) {
        this.secretsManagerService = secretsManagerService;
    }

    public void setEndpointDiscoveryService(EndpointDiscoveryService endpointDiscoveryService) {
        this.endpointDiscoveryService = endpointDiscoveryService;
    }

    @PostConstruct
    public void initializeServices() {
        initializeServices(false);
    }

    private void initializeServices(boolean throwEndpointException) {
        try {
            endpointDiscoveryService.obtainConfiguration(httpServletRequest.getHeader("x-host"), httpServletRequest.getHeader("x-basic-auth-username"), httpServletRequest.getHeader("x-basic-auth-password"));
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        String basicAuthUsername = httpServletRequest.getHeader("x-basic-auth-username");
        String basicAuthPassword = httpServletRequest.getHeader("x-basic-auth-password");

        initializeEventServicePortType(basicAuthUsername, basicAuthPassword);
        initializeCardServicePortType(basicAuthUsername, basicAuthPassword);
        initializeCertificateServicePortType(basicAuthUsername, basicAuthPassword);
    }

    public void initializeCertificateServicePortType(String basicAuthUsername, String basicAuthPassword) {
        de.gematik.ws.conn.certificateservice.wsdl.v6.CertificateServicePortType service =
                new de.gematik.ws.conn.certificateservice.wsdl.v6.CertificateService(
                        getClass().getResource("/CertificateService.wsdl")).getCertificateServicePort();
        BindingProvider bp = (BindingProvider) service;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                endpointDiscoveryService.getCertificateServiceEndpointAddress());
        if (basicAuthUsername != null && basicAuthPassword != null) {
            bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, basicAuthUsername);
            bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, basicAuthPassword);
        }
        configureBindingProvider(bp);

        certificateServicePortType = service;
    }

    public void initializeEventServicePortType(String basicAuthUsername, String basicAuthPassword) {
        de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType service =
                new de.gematik.ws.conn.eventservice.wsdl.v7.EventService(
                        getClass().getResource("/EventService.wsdl")).getEventServicePort();
        BindingProvider bp = (BindingProvider) service;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                endpointDiscoveryService.getEventServiceEndpointAddress());
        if (basicAuthUsername != null && basicAuthPassword != null) {
            bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, basicAuthUsername);
            bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, basicAuthPassword);
        }
        configureBindingProvider(bp);

        eventServicePortType = service;
    }

    public void initializeCardServicePortType(String basicAuthUsername, String basicAuthPassword) {
        de.gematik.ws.conn.cardservice.wsdl.v8.CardServicePortType service =
                new de.gematik.ws.conn.cardservice.wsdl.v8.CardService(
                        getClass().getResource("/CardService.wsdl")).getCardServicePort();
        BindingProvider bp = (BindingProvider) service;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                endpointDiscoveryService.getCardServiceEndpointAddress());
        if (basicAuthUsername != null && basicAuthPassword != null) {
            bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, basicAuthUsername);
            bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, basicAuthPassword);
        }
        configureBindingProvider(bp);

        cardServicePortType = service;
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

    @Produces
    public de.gematik.ws.conn.certificateservice.wsdl.v6.CertificateServicePortType getCertificateServicePortType() {
        return this.certificateServicePortType;
    }

    @Produces
    public de.gematik.ws.conn.cardservice.wsdl.v8.CardServicePortType getCardServicePortType() {
        return this.cardServicePortType;
    }
}
