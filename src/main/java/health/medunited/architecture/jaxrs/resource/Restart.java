package health.medunited.architecture.jaxrs.resource;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import health.medunited.architecture.service.endpoint.SSLUtilities;

@Path("restart")
public class Restart {

    @POST
    public void restart() {
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();

        clientBuilder.connectTimeout(3, TimeUnit.SECONDS);
        clientBuilder.readTimeout(3, TimeUnit.SECONDS);

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
        WebTarget loginTarget = client.target("https://192.168.178.42:8500")
                .path("/rest/mgmt/ak/konten/login");

        WebTarget restartTarget = client.target("https://192.168.178.42:8500")
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
