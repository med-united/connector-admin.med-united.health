package health.medunited.architecture.jaxrs.resource;

import org.apache.http.client.HttpClient;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.junit.jupiter.api.Test;
import health.medunited.architecture.jaxrs.management.RiseConnector;

public class RiseConnectorTest {

    String host = "https://192.168.178.75:8443";

    RiseConnector riseConnector = new RiseConnector();
    @Test
    public void testAcceptingAllConnections() throws Exception {
        ApacheHttpClient4Executor apacheHttpClient4Executor =
                new ApacheHttpClient4Executor(riseConnector.createAllTrustingClient());

        ClientRequest clientRequest = new ClientRequest(host, apacheHttpClient4Executor);

        System.out.println(clientRequest.getUri()+" "+clientRequest.getHeaders());
    }

}
