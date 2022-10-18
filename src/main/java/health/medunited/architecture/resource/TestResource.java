package health.medunited.architecture.resource;

import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventService;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;
import de.gematik.ws.conn.eventservice.wsdl.v7.FaultMessage;
import de.gematik.ws.conn.signatureservice.wsdl.v7.SignatureServicePortTypeV755;
import de.gematik.ws.conn.signatureservice.wsdl.v7.SignatureServiceV755;
import health.medunited.architecture.service.endpoint.SSLUtilities;
import health.medunited.architecture.z.*;

import javax.inject.Inject;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509KeyManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
import java.util.logging.Level;
import java.util.regex.PatternSyntaxException;


@Path("/test")
public class TestResource {

    @Inject
    DefaultConnectorServicesProvider defaultConnectorServicesProvider;

    @Context
    HttpServletRequest httpServletRequest;

    @Inject
    SecretsManagerService secretsManagerService;

    @Inject
    EndpointDiscoveryService endpointDiscoveryService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response status() throws FaultMessage, CertificateException, URISyntaxException, KeyStoreException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, KeyManagementException {
        RuntimeConfig runtimeConfig = new RuntimeConfig(httpServletRequest);
        GetCards parameter = new GetCards();
        parameter.setContext(getContextType(runtimeConfig));

        secretsManagerService.setUpSSLContext(getKeyFromKeyStoreUri("data:application/x-pkcs12;base64,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", "N4rouwibGRhne2Fa"));

        EventServicePortType service = initializeEventServicePortType();
        GetCardsResponse r = service.getCards(parameter);
        return Response.ok().build();
    }

    public ContextType getContextType(UserConfig userConfig) {
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

}
