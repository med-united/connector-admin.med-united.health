package health.medunited.architecture.service;

import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventService;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;
import de.gematik.ws.conn.eventservice.wsdl.v7.FaultMessage;
import health.medunited.architecture.service.endpoint.SSLUtilities;
import health.medunited.architecture.z.DefaultConnectorServicesProvider;
import health.medunited.architecture.z.RuntimeConfig;
import health.medunited.architecture.z.SecretsManagerService;
import health.medunited.architecture.z.UserConfig;

import javax.enterprise.inject.Alternative;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509KeyManager;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.BindingProvider;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.regex.PatternSyntaxException;

@Alternative
public class StatusService {

    private ContextType contextType;
    private String url;
    private String sslCertificate;
    private String sslCertificatePassword;
    private SecretsManagerService secretsManagerService;
    private DefaultConnectorServicesProvider defaultConnectorServicesProvider;

    public StatusService(ContextType contextType, String url, String sslCertificate, String sslCertificatePassword,
                         SecretsManagerService secretsManagerService,
                         DefaultConnectorServicesProvider defaultConnectorServicesProvider) {
        this.contextType = contextType;
        this.url = url;
        this.sslCertificate = sslCertificate;
        this.sslCertificatePassword = sslCertificatePassword;
        this.secretsManagerService = secretsManagerService;
        this.defaultConnectorServicesProvider = defaultConnectorServicesProvider;
    }

    public StatusService() {
    }

    public String getStatus(HttpServletRequest httpServletRequest) throws CertificateException, URISyntaxException, KeyStoreException, NoSuchAlgorithmException, IOException, FaultMessage, UnrecoverableKeyException, KeyManagementException {
        RuntimeConfig runtimeConfig = new RuntimeConfig();
        GetCards parameter = new GetCards();
        parameter.setContext(getContextType(runtimeConfig));

        secretsManagerService.setUpSSLContext(getKeyFromKeyStoreUri(getSslCertificate(), getSslCertificatePassword()));

        EventServicePortType service = initializeEventServicePortType();
        GetCardsResponse r = service.getCards(parameter);
        return "OK";
    }

    private ContextType getContextType(UserConfig userConfig) {
        if(userConfig == null) {
            return defaultConnectorServicesProvider.getContextType();
        }
        ContextType contextType = new ContextType();
        contextType.setMandantId("Incentergy");
        contextType.setClientSystemId("Incentergy");
        contextType.setWorkplaceId("1786_A1");
        contextType.setUserId("42401d57-15fc-458f-9079-79f6052abad9");
        return contextType;
    }

    private EventServicePortType initializeEventServicePortType() {
        EventServicePortType service = new EventService(getClass().getResource("/EventService.wsdl"))
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

        return service;
    }

    private void configureBindingProvider(BindingProvider bindingProvider) {
        SSLContext sslContext = secretsManagerService.getSslContext();
        if(sslContext != null) {
            bindingProvider.getRequestContext().put("com.sun.xml.ws.transport.https.client.SSLSocketFactory",
                    sslContext.getSocketFactory());
        }
        bindingProvider.getRequestContext().put("com.sun.xml.ws.transport.https.client.hostname.verifier",
                new SSLUtilities.FakeHostnameVerifier());

//        String basicAuthUsername = getUserConfig().getConfigurations().getBasicAuthUsername();
//        String basicAuthPassword = getUserConfig().getConfigurations().getBasicAuthPassword();
//
//        if(basicAuthUsername != null && !basicAuthUsername.equals("")) {
//            bindingProvider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, basicAuthUsername);
//            bindingProvider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, basicAuthPassword);
//        }
    }

    private KeyManager getKeyFromKeyStoreUri(String keystoreUri, String keystorePassword) throws URISyntaxException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        if(keystorePassword== null) {
            keystorePassword = "";
        }
        String keyAlias = null;
        KeyStore store = KeyStore.getInstance("pkcs12");

        URI uriParser = new URI(keystoreUri);
        String scheme = uriParser.getScheme();

        if (scheme.equalsIgnoreCase("data")){
            // example: "data:application/x-pkcs12;base64,MIACAQMwgAY...gtc/qoCAwGQAAAA"
            String[] schemeSpecificParts = uriParser.getSchemeSpecificPart().split(";");
            String contentType = schemeSpecificParts[0];
            if (contentType.equalsIgnoreCase("application/x-pkcs12")){
                String[] dataParts = schemeSpecificParts[1].split(",");
                String encodingType = dataParts[0];
                if (encodingType.equalsIgnoreCase("base64")){
                    String keystoreBase64 = dataParts[1];
                    ByteArrayInputStream keystoreInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(keystoreBase64));
                    store.load(keystoreInputStream, keystorePassword.toCharArray());
                }
            }
        } else if (scheme.equalsIgnoreCase("file")) {
            String keystoreFile = uriParser.getPath();
            String query = uriParser.getRawQuery();
            try {
                String[] queryParts = query.split("=");
                String parameterName = queryParts[0];
                String parameterValue = queryParts[1];
                if (parameterName.equalsIgnoreCase("alias")){
                    // example: "file:src/test/resources/certs/keystore.p12?alias=key2"
                    keyAlias = parameterValue;
                }
            } catch (NullPointerException| PatternSyntaxException e){
                // take the first key from KeyStore, whichever it is
                // example: "file:src/test/resources/certs/keystore.p12"
            }
            FileInputStream in = new FileInputStream(keystoreFile);
            store.load(in, keystorePassword.toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        try {
            kmf.init(store, keystorePassword.toCharArray());
            final X509KeyManager origKm = (X509KeyManager)kmf.getKeyManagers()[0];
            if(keyAlias == null) {
                return origKm;
            } else {
                final String finalKeyAlias = keyAlias;
                return new X509KeyManager() {

                    @Override
                    public String chooseClientAlias(String[] arg0, Principal[] arg1, Socket arg2) {
                        return finalKeyAlias;
                    }

                    @Override
                    public String chooseServerAlias(String arg0, Principal[] arg1, Socket arg2) {
                        return origKm.chooseServerAlias(arg0, arg1, arg2);
                    }

                    @Override
                    public X509Certificate[] getCertificateChain(String alias) {
                        return origKm.getCertificateChain(alias);
                    }

                    @Override
                    public String[] getClientAliases(String arg0, Principal[] arg1) {
                        return origKm.getClientAliases(arg0, arg1);
                    }

                    @Override
                    public PrivateKey getPrivateKey(String alias) {
                        return origKm.getPrivateKey(alias);
                    }

                    @Override
                    public String[] getServerAliases(String arg0, Principal[] arg1) {
                        return origKm.getServerAliases(arg0, arg1);
                    }
                };
            }
        } catch (UnrecoverableKeyException e) {
            return null;
        }
    }

    public ContextType getContextType() {
        return contextType;
    }

    public void setContextType(ContextType contextType) {
        this.contextType = contextType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSslCertificate() {
        return sslCertificate;
    }

    public void setSslCertificate(String sslCertificate) {
        this.sslCertificate = sslCertificate;
    }

    public String getSslCertificatePassword() {
        return sslCertificatePassword;
    }

    public void setSslCertificatePassword(String sslCertificatePassword) {
        this.sslCertificatePassword = sslCertificatePassword;
    }

    public SecretsManagerService getSecretsManagerService() {
        return secretsManagerService;
    }

    public void setSecretsManagerService(SecretsManagerService secretsManagerService) {
        this.secretsManagerService = secretsManagerService;
    }
}
