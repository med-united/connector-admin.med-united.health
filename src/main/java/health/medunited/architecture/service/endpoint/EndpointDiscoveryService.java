package health.medunited.architecture.service.endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import de.gematik.ws.conn.servicedirectory.v3.ConnectorServices;
import de.gematik.ws.conn.serviceinformation.v2.ServiceType;
import health.medunited.architecture.service.common.security.SecretsManagerService;

@RequestScoped
public class EndpointDiscoveryService {

  private static final Logger log = Logger.getLogger(EndpointDiscoveryService.class.getName());

  SecretsManagerService secretsManagerService;

  private ConnectorServices connectorSds;

  public EndpointDiscoveryService() {}

  @Inject
  public EndpointDiscoveryService(SecretsManagerService secretsManagerService) {
    this.secretsManagerService = secretsManagerService;
  }

  public byte[] obtainFile(String connectorBaseUrl, String basicAuthUsername,
      String basicAuthPassword) {
    Client client = buildClient(3);
    Invocation invocation =
        buildInvocation(client, connectorBaseUrl, basicAuthUsername, basicAuthPassword);
    try {
      return invocation.invoke(InputStream.class).readAllBytes();
    } catch (IOException e) {
      log.log(Level.SEVERE, "Could not read connector.sds", e);
      return new byte[0];
    } finally {
      client.close();
    }
  }

  public ConnectorServices obtainConfiguration(String connectorBaseUrl, String basicAuthUsername,
      String basicAuthPassword) {
    Client client = buildClient(3);
    Invocation invocation =
        buildInvocation(client, connectorBaseUrl, basicAuthUsername, basicAuthPassword);
    try {
      InputStream inputStream = invocation.invoke(InputStream.class);
      return parseInput(inputStream);
    } catch (JAXBException | ProcessingException | IllegalArgumentException e) {
      throw new IllegalStateException(
          "Could not get or parse connector.sds from " + connectorBaseUrl, e);
    } finally {
      client.close();
    }
  }

  public ConnectorServices parseInput(InputStream inputStream) throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(ConnectorServices.class);
    this.connectorSds = (ConnectorServices) jaxbContext.createUnmarshaller().unmarshal(inputStream);
    return this.connectorSds;
  }

  private Invocation buildInvocation(Client client, String connectorBaseUrl,
      String basicAuthUsername, String basicAuthPassword) {
    Invocation.Builder builder = client.target(connectorBaseUrl).path("/connector.sds").request();
    if (basicAuthUsername != null && basicAuthPassword != null) {
      builder.header("Authorization", "Basic " + Base64.getEncoder()
          .encodeToString((basicAuthUsername + ":" + basicAuthPassword).getBytes()));
    }
    return builder.buildGet();
  }

  private Client buildClient(long timeoutSeconds) {
    return ClientBuilder.newBuilder().connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
        .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
        .sslContext(secretsManagerService.getSslContext())
        .hostnameVerifier(new SSLUtilities.FakeHostnameVerifier()).build();
  }

  public String getEventServiceEndpointAddress() {
    return getEndpointAddress("EventService");
  }

  public String getCardServiceEndpointAddress() {
    return getEndpointAddress("CardService");
  }

  public String getCertificateServiceEndpointAddress() {
    return getEndpointAddress("CertificateService");
  }

  private String getEndpointAddress(String serviceName) {
    List<ServiceType> services = connectorSds.getServiceInformation().getService();
    for (ServiceType service : services) {
      if (service.getName().equals(serviceName)) {
        return service.getVersions().getVersion().get(0).getEndpointTLS().getLocation();
      }
    }
    throw new IllegalArgumentException("Service not found in connector.sds by name:" + serviceName);
  }

}
