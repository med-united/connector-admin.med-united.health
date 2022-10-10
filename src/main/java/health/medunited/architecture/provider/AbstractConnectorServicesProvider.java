package health.medunited.architecture.provider;

import de.gematik.ws.conn.authsignatureservice.wsdl.v7.AuthSignatureService;
import de.gematik.ws.conn.authsignatureservice.wsdl.v7.AuthSignatureServicePortType;
import de.gematik.ws.conn.cardservice.wsdl.v8.CardService;
import de.gematik.ws.conn.cardservice.wsdl.v8.CardServicePortType;
import de.gematik.ws.conn.certificateservice.wsdl.v6.CertificateService;
import de.gematik.ws.conn.certificateservice.wsdl.v6.CertificateServicePortType;
import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventService;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;
import de.gematik.ws.conn.signatureservice.wsdl.v7.SignatureServicePortTypeV740;
import de.gematik.ws.conn.signatureservice.wsdl.v7.SignatureServicePortTypeV755;
import de.gematik.ws.conn.signatureservice.wsdl.v7.SignatureServiceV740;
import de.gematik.ws.conn.signatureservice.wsdl.v7.SignatureServiceV755;
import de.gematik.ws.conn.vsds.vsdservice.v5.VSDService;
import de.gematik.ws.conn.vsds.vsdservice.v5.VSDServicePortType;
import health.medunited.architecture.context.ConnectorScopeContext;
import health.medunited.architecture.service.common.security.SecretsManagerService;
import health.medunited.architecture.service.endpoint.EndpointDiscoveryService;
import health.medunited.architecture.service.endpoint.SSLUtilities;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.xml.ws.BindingProvider;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public abstract class AbstractConnectorServicesProvider {
    private final static Logger log = Logger.getLogger(AbstractConnectorServicesProvider.class.getName());
 
    @Inject
    EndpointDiscoveryService endpointDiscoveryService;
    @Inject
    SecretsManagerService secretsManagerService;

    private VSDServicePortType vSDServicePortType;
    private CardServicePortType cardServicePortType;
    private CertificateServicePortType certificateService;
    private EventServicePortType eventServicePortType;
    private AuthSignatureServicePortType authSignatureServicePortType;
    private SignatureServicePortTypeV740 signatureServicePortType;
    private SignatureServicePortTypeV755 signatureServicePortTypeV755;
    private ContextType contextType;

    public void initializeServices() {
        initializeServices(false);
    }

    public void initializeServices(boolean throwEndpointException) {
        if(endpointDiscoveryService != null) {
            try {
                endpointDiscoveryService.obtainConfiguration(throwEndpointException);
                initializeVSDServicePortType();
                initializeCardServicePortType();
                initializeCertificateService();
                initializeEventServicePortType();
                initializeAuthSignatureServicePortType();
                initializeSignatureServicePortType();
                initializeSignatureServicePortTypeV755();
            } catch (Exception e) {
                vSDServicePortType = null;
                cardServicePortType = null;
                certificateService = null;
                eventServicePortType = null;
                authSignatureServicePortType = null;
                signatureServicePortType = null;
                signatureServicePortTypeV755 = null;
                if(throwEndpointException) {
                    throw new RuntimeException(e);
                } else {
                    log.log(Level.SEVERE, "Could not obtainConfiguration", e);
                }
            }
            initializeContextType();
        } else {
            log.warning("endpointDiscoveryService is null");
        }
    }
    
    private void initializeVSDServicePortType() {
        VSDServicePortType vsdService = new VSDService(getClass().getResource("/vsds/VSDService.wsdl"))
                .getVSDServicePort();

        BindingProvider bp = (BindingProvider) vsdService;
        if(endpointDiscoveryService.getVSDServiceEndpointAddress() != null) {
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                    endpointDiscoveryService.getVSDServiceEndpointAddress());
        } else {
            log.warning("VSDServiceEndpointAddress is null");
        }
        configureBindingProvider(bp);

        vSDServicePortType = vsdService;
    }

    private void initializeCardServicePortType() {
        CardServicePortType cardService = new CardService(getClass().getResource("/CardService.wsdl"))
                .getCardServicePort();

        BindingProvider bp = (BindingProvider) cardService;
        if(endpointDiscoveryService.getCardServiceEndpointAddress() != null) {
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                    endpointDiscoveryService.getCardServiceEndpointAddress());
        } else {
            log.warning("CardServiceEndpointAddress is null");
        }
        configureBindingProvider(bp);

        cardServicePortType = cardService;
    }

    private void initializeCertificateService() {
        CertificateServicePortType service = new CertificateService(getClass()
                .getResource("/CertificateService_v6_0_1.wsdl")).getCertificateServicePort();

        BindingProvider bp = (BindingProvider) service;
        if(endpointDiscoveryService.getCertificateServiceEndpointAddress() != null) {
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                endpointDiscoveryService.getCertificateServiceEndpointAddress());
        } else {
            log.warning("CertificateServiceEndpointAddress is null");
        }
        configureBindingProvider(bp);

        this.certificateService = service;
    }

    private void initializeEventServicePortType() {
        EventServicePortType service = new EventService(getClass().getResource("/EventService.wsdl"))
                .getEventServicePort();

        BindingProvider bp = (BindingProvider) service;
        if(endpointDiscoveryService.getEventServiceEndpointAddress() != null) {
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                    endpointDiscoveryService.getEventServiceEndpointAddress());
        } else {
            log.warning("EventServiceEndpointAddress is null");
        }
        configureBindingProvider(bp);

        eventServicePortType = service;
    }

    private void initializeAuthSignatureServicePortType() {
        AuthSignatureServicePortType service = new AuthSignatureService(getClass().getResource(
                "/AuthSignatureService_v7_4_1.wsdl")).getAuthSignatureServicePort();
        BindingProvider bp = (BindingProvider) service;
        if(endpointDiscoveryService.getAuthSignatureServiceEndpointAddress() != null) {
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                    endpointDiscoveryService.getAuthSignatureServiceEndpointAddress());
        } else {
            log.warning("AuthSignatureServiceEndpointAddress is null");
        }
        configureBindingProvider(bp);

        authSignatureServicePortType = service;
    }

    private void initializeSignatureServicePortType() {
        SignatureServicePortTypeV740 service = new SignatureServiceV740(getClass()
                .getResource("/SignatureService.wsdl")).getSignatureServicePortV740();

        BindingProvider bp = (BindingProvider) service;
        if(endpointDiscoveryService.getSignatureServiceEndpointAddress() != null) {
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                endpointDiscoveryService.getSignatureServiceEndpointAddress());
        } else {
            log.warning("SignatureServiceEndpointAddress is null");
        }
        configureBindingProvider(bp);

        signatureServicePortType = service;
    }

    private void initializeSignatureServicePortTypeV755() {
        SignatureServicePortTypeV755 service = new SignatureServiceV755(getClass()
                .getResource("/SignatureService_V7_5_5.wsdl")).getSignatureServicePortTypeV755();

        BindingProvider bp = (BindingProvider) service;
        if(endpointDiscoveryService.getSignatureServiceEndpointAddress() != null) {
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                endpointDiscoveryService.getSignatureServiceEndpointAddress());
        } else {
            log.warning("SignatureServiceEndpointAddress for V755 is null");
        }
        configureBindingProvider(bp);

        signatureServicePortTypeV755 = service;
    }

    private void initializeContextType() {
        ContextType contextType = new ContextType();
        contextType.setMandantId(getConnectorScopeContext().getMandantId());
        contextType.setClientSystemId(getConnectorScopeContext().getClientSystemId());
        contextType.setWorkplaceId(getConnectorScopeContext().getWorkplaceId());
        contextType.setUserId(getConnectorScopeContext().getUserId());

        this.contextType = contextType;
    }

    private void configureBindingProvider(BindingProvider bindingProvider) {
        SSLContext sslContext = secretsManagerService.getSslContext();
        if(sslContext != null) {
            bindingProvider.getRequestContext().put("com.sun.xml.ws.transport.https.client.SSLSocketFactory",
                    sslContext.getSocketFactory());
        }
        bindingProvider.getRequestContext().put("com.sun.xml.ws.transport.https.client.hostname.verifier",
                new SSLUtilities.FakeHostnameVerifier());

        String basicAuthUsername = getConnectorScopeContext().getBasicAuthUsername();
        String basicAuthPassword = getConnectorScopeContext().getBasicAuthPassword();

        if(basicAuthUsername != null && !basicAuthUsername.equals("")) {
            bindingProvider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, basicAuthUsername);
            bindingProvider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, basicAuthPassword);
        }
    }

    public EventServicePortType getEventServicePortType() {
        return eventServicePortType;
    }

    public abstract ConnectorScopeContext getConnectorScopeContext();
}
