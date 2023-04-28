package health.medunited.architecture.jaxrs.management;

import health.medunited.architecture.service.endpoint.SSLUtilities;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Named("secunet")
public class SecunetConnector implements Connector{

    private static final Logger log = Logger.getLogger(SecunetConnector.class.getName());

    @Override
    public void restart(String connectorUrl, String managementPort) {
        log.log(Level.INFO, "Restarting Secunet connector");

        //TODO: This is only a test to prove that it works. We have to change it with security in mind and pass the parameters from the HTTP Request
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        clientBuilder.hostnameVerifier(new SSLUtilities.FakeHostnameVerifier());

        // Create a TrustManager that trusts all certificates
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Configure the client to use the TrustManager
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        clientBuilder.sslContext(sslContext);
        clientBuilder.hostnameVerifier((hostname, session) -> true);

        Client client = clientBuilder.build();
        WebTarget loginTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/rest/mgmt/ak/konten/login");

        WebTarget restartTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/rest/mgmt/nk/system");

        Invocation.Builder loginBuilder = loginTarget.request(MediaType.APPLICATION_JSON);
        Response loginResponse = loginBuilder.post(Entity.json("{\n" +
                "    \"username\": \"fakeusername\",\n" +
                "    \"password\": \"fakepassword\"\n" +
                "}"));

        Invocation.Builder restartBuilder = restartTarget.request(MediaType.APPLICATION_JSON);
        restartBuilder.header("Authorization", loginResponse.getHeaders().get("Authorization").get(0));
        restartBuilder.post(Entity.json(""));
    }
}
