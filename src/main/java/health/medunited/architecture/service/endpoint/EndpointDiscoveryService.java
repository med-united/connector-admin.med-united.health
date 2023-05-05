package health.medunited.architecture.service.endpoint;

import java.io.IOException;
import java.io.InputStream;
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

import de.gematik.ws._int.version.productinformation.v1.ProductTypeInformation;
import de.gematik.ws.conn.servicedirectory.v3.ConnectorServices;
import de.gematik.ws.conn.serviceinformation.v2.ServiceType;
import health.medunited.architecture.service.common.security.SecretsManagerService;

@RequestScoped
public class EndpointDiscoveryService {

    private static final Logger log = Logger.getLogger(EndpointDiscoveryService.class.getName());

    @Inject
    SecretsManagerService secretsManagerService;

    private String eventServiceEndpointAddress;

    private String cardServiceEndpointAddress;

    private String certificateServiceEndpointAddress;

    private ConnectorServices connectorSds;

    private ProductTypeInformation productTypeInformation;

    public void setSecretsManagerService(SecretsManagerService secretsManagerService) {
        this.secretsManagerService = secretsManagerService;
    }

    public byte[] obtainFile(String connectorBaseUrl) {
        Client client = buildClient();
        Invocation invocation = buildInvocation(client, connectorBaseUrl);
        try {
            return invocation
                .invoke(InputStream.class)
                .readAllBytes();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not read connector.sds", e);
            return new byte[0];
        } finally {
            client.close();
        }
    }

    public void obtainConfiguration(String connectorBaseUrl) throws Exception {
        Client client = buildClient();
        Invocation invocation = buildInvocation(client, connectorBaseUrl);
        try {
            InputStream inputStream = invocation.invoke(InputStream.class);
            parseInput(inputStream);
        } catch (JAXBException | ProcessingException | IllegalArgumentException e) {
            throw new IllegalStateException("Could not get or parse connector.sds from "+connectorBaseUrl, e);
        } finally {
            client.close();
        }
    }

    public void parseInput(InputStream inputStream) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ConnectorServices.class);
        connectorSds = (ConnectorServices) jaxbContext.createUnmarshaller().unmarshal(inputStream);

        productTypeInformation = connectorSds.getProductInformation().getProductTypeInformation();

        List<ServiceType> services = connectorSds.getServiceInformation().getService();

        for (ServiceType service : services) {
            String serviceName = service.getName();
            switch (serviceName) {
                case "EventService": {
                    eventServiceEndpointAddress = service.getVersions().getVersion().get(0).getEndpointTLS().getLocation();
                    break;
                }
                case "CardService": {
                    cardServiceEndpointAddress = service.getVersions().getVersion().get(0).getEndpointTLS().getLocation();
                    break;
                }
                case "CertificateService": {
                    certificateServiceEndpointAddress = service.getVersions().getVersion().get(0).getEndpointTLS().getLocation();
                    break;
                }
            }
        }
    }

    private Invocation buildInvocation(Client client, String connectorBaseUrl) {
        Invocation.Builder builder = client
                .target(connectorBaseUrl)
                .path("/connector.sds")
                .request();
        return builder.buildGet();
    }

    private Client buildClient() {
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        clientBuilder.connectTimeout(3, TimeUnit.SECONDS);
        clientBuilder.readTimeout(3, TimeUnit.SECONDS);
        clientBuilder.sslContext(secretsManagerService.getSslContext());
        clientBuilder = clientBuilder.hostnameVerifier(new SSLUtilities.FakeHostnameVerifier());
        return clientBuilder.build();
    }

    public String getEventServiceEndpointAddress() {
        return eventServiceEndpointAddress;
    }

    public String getCardServiceEndpointAddress() {
        return cardServiceEndpointAddress;
    }

    public String getCertificateServiceEndpointAddress() {
        return certificateServiceEndpointAddress;
    }

    public ProductTypeInformation ProductTypeInformation() {
        return productTypeInformation;
    }

    public ConnectorServices getConnectorSds() {
        return connectorSds;
    }

}
