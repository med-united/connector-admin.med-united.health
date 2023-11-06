package health.medunited.cat.base.jaxrs.resource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import health.medunited.cat.base.service.endpoint.EndpointDiscoveryService;

@Path("sds")
public class ConnectorSds {

  @Inject
  EndpointDiscoveryService endpointDiscoveryService;

  @GET
  @Path("config")
  public Response connectorSdsConfig(@HeaderParam("X-Host") String connectorUrl,
      @HeaderParam("x-basic-auth-username") String basicAuthUsername,
      @HeaderParam("x-basic-auth-password") String basicAuthPassword) {
    try {
      return Response
          .status(Response.Status.OK)
          .entity(endpointDiscoveryService.obtainConfiguration(connectorUrl, basicAuthUsername,
              basicAuthPassword))
          .type(MediaType.APPLICATION_JSON_TYPE)
          .build();
    } catch (Exception e) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

  }

  @GET
  @Path("file")
  public byte[] connectorSdsFile(@HeaderParam("X-Host") String connectorBaseUrl,
      @HeaderParam("x-basic-auth-username") String basicAuthUsername,
      @HeaderParam("x-basic-auth-password") String basicAuthPassword) {
    return endpointDiscoveryService.obtainFile(connectorBaseUrl, basicAuthUsername,
        basicAuthPassword);
  }

}