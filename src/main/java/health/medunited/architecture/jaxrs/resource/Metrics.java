package health.medunited.architecture.jaxrs.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;

@Path("/metrics/application")
public class Metrics {

    Client client = ClientBuilder.newClient();

    @GET
    public String metrics() {
        return client.target("http://localhost:9990/metrics/application").request(MediaType.APPLICATION_JSON).get(String.class);
    }
}
