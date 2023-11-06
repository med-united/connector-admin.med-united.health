package health.medunited.cat.base.jaxrs.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

@Path("/metrics/application")
public class Metrics {

  Client client = ClientBuilder.newClient();

  @GET
  public String metrics() {
    return client.target("http://localhost:9990/metrics/application")
        .request(MediaType.APPLICATION_JSON).get(String.class);
  }
}
