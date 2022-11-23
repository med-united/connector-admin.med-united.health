package health.medunited.architecture.z;

import health.medunited.architecture.service.endpoint.SSLUtilities;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;

@RequestScoped
public class SecretsManagerService {

    private static final Logger log = Logger.getLogger(SecretsManagerService.class.getName());

    private SSLContext sslContext;

    @Inject
    HttpServletRequest request;

    @PostConstruct
    public void createSSLContext() {
        try {
            String keystore = request.getHeader("X-ClientCertificate");
            String keystorePassword = request.getHeader("X-ClientCertificatePassword");
            setUpSSLContext(getKeyFromKeyStoreUri(keystore, keystorePassword));
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | URISyntaxException | IOException
                | KeyManagementException e) {
            log.severe("There was a problem when unpacking key from ClientCertificateKeyStore:");
            e.printStackTrace();
        }
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    private void setUpSSLContext(KeyManager km)
            throws NoSuchAlgorithmException, KeyManagementException {
        sslContext = SSLContext.getInstance(SslContextType.TLS.getSslContextType());

        sslContext.init(new KeyManager[]{km}, new TrustManager[]{new SSLUtilities.FakeX509TrustManager()},
                null);
    }

    private KeyManager getKeyFromKeyStoreUri(String keystoreUri, String keystorePassword)
            throws URISyntaxException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        if (keystorePassword == null) {
            keystorePassword = "";
        }
        String keyAlias = null;
        KeyStore store = KeyStore.getInstance("pkcs12");

        URI uriParser = new URI(keystoreUri);
        String scheme = uriParser.getScheme();

        if (scheme.equalsIgnoreCase("data")) {
            // example: "data:application/x-pkcs12;base64,MIACAQMwgAY...gtc/qoCAwGQAAAA"
            String[] schemeSpecificParts = uriParser.getSchemeSpecificPart().split(";");
            String contentType = schemeSpecificParts[0];
            if (contentType.equalsIgnoreCase("application/x-pkcs12")) {
                String[] dataParts = schemeSpecificParts[1].split(",");
                String encodingType = dataParts[0];
                if (encodingType.equalsIgnoreCase("base64")) {
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
                if (parameterName.equalsIgnoreCase("alias")) {
                    // example: "file:src/test/resources/certs/keystore.p12?alias=key2"
                    keyAlias = parameterValue;
                }
            } catch (NullPointerException | PatternSyntaxException e) {
                // take the first key from KeyStore, whichever it is
                // example: "file:src/test/resources/certs/keystore.p12"
            }
            FileInputStream in = new FileInputStream(keystoreFile);
            store.load(in, keystorePassword.toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        try {
            kmf.init(store, keystorePassword.toCharArray());
            final X509KeyManager origKm = (X509KeyManager) kmf.getKeyManagers()[0];
            if (keyAlias == null) {
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

    public enum SslContextType {
        SSL("SSL"), TLS("TLS");

        private final String sslContextType;

        SslContextType(String sslContextType) {
            this.sslContextType = sslContextType;
        }

        public String getSslContextType() {
            return sslContextType;
        }
    }

    public enum KeyStoreType {
        JKS("jks"), PKCS12("pkcs12");

        private final String keyStoreType;

        KeyStoreType(String keyStoreType) {
            this.keyStoreType = keyStoreType;
        }

        public String getKeyStoreType() {
            return keyStoreType;
        }
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
