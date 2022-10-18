package health.medunited.architecture.z;

import health.medunited.architecture.service.endpoint.SSLUtilities;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.logging.Logger;

@ApplicationScoped
public class SecretsManagerService {

    private static final Logger log = Logger.getLogger(SecretsManagerService.class.getName());

    @Inject
    AppConfig appConfig;

    @Inject
    UserConfigurationService userConfigurationService;

    @Inject
    Event<Exception> exceptionEvent;

    private SSLContext sslContext;


    public SecretsManagerService() {
    }

    @PostConstruct
    void createSSLContext() {

        acceptAllCertificates();

    }

    public SSLContext createSSLContext(UserConfigurations userConfigurations) {
        String clientCertificateString = userConfigurations.getClientCertificate().substring(33);
        byte[] clientCertificateBytes = Base64.getDecoder().decode(clientCertificateString);
        try (ByteArrayInputStream certificateInputStream = new ByteArrayInputStream(clientCertificateBytes)) {
            return createSSLContext(userConfigurations.getClientCertificatePassword(), certificateInputStream);
        } catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException
                | UnrecoverableKeyException | KeyManagementException e) {
            log.severe("There was a problem when creating the SSLContext:");
            e.printStackTrace();
            return null;
        }
    }

    public void acceptAllCertificates() {
        // For the connector trust all certificates
        try {
            sslContext = SSLContext.getInstance(SslContextType.TLS.getSslContextType());
            sslContext.init(null, new TrustManager[]{new SSLUtilities.FakeX509TrustManager()},
                    null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.severe("There was a problem when creating the SSLContext:");
            e.printStackTrace();
        }
    }

    public void initFromAppConfig() {
        String connectorTlsCertAuthStoreFile = appConfig.getCertAuthStoreFile().get();
        String connectorTlsCertAuthStorePwd = appConfig.getCertAuthStoreFilePassword().get();

        try (FileInputStream certificateInputStream = new FileInputStream(connectorTlsCertAuthStoreFile)) {
            sslContext = createSSLContext(connectorTlsCertAuthStorePwd, certificateInputStream);
        } catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException
                | UnrecoverableKeyException | KeyManagementException e) {
            log.severe("There was a problem when creating the SSLContext:");
            e.printStackTrace();
        }
    }

    public SSLContext createSSLContext(String connectorTlsCertAuthStorePwd, InputStream certificateInputStream)
            throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException,
            UnrecoverableKeyException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance(SslContextType.TLS.getSslContextType());

        KeyStore ks = KeyStore.getInstance(KeyStoreType.PKCS12.getKeyStoreType());
        ks.load(certificateInputStream, connectorTlsCertAuthStorePwd.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, connectorTlsCertAuthStorePwd.toCharArray());

        sslContext.init(kmf.getKeyManagers(), new TrustManager[]{new SSLUtilities.FakeX509TrustManager()},
                null);
        return sslContext;
    }

    public void setUpSSLContext(KeyManager km)
            throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException,
            UnrecoverableKeyException, KeyManagementException {
        sslContext = SSLContext.getInstance(SslContextType.TLS.getSslContextType());

        sslContext.init(new KeyManager[]{km}, new TrustManager[]{new SSLUtilities.FakeX509TrustManager()},
                null);
    }

    public void updateSSLContext() {
        createSSLContext();
    }

    public SSLContext getSslContext() {
        return sslContext;
    }


    KeyStore initializeTrustStoreFromInputStream(InputStream trustStoreInputStream,
                                                 KeyStoreType keyStoreType,
                                                 char[] keyStorePassword)
            throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore trustStore = KeyStore.getInstance(keyStoreType.getKeyStoreType());
        trustStore.load(trustStoreInputStream, keyStorePassword);

        return trustStore;
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
}
