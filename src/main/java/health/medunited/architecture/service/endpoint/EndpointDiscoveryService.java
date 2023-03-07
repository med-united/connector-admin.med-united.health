package health.medunited.architecture.service.endpoint;

import health.medunited.architecture.service.common.security.SecretsManagerService;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequestScoped
public class EndpointDiscoveryService {

    private static final Logger log = Logger.getLogger(EndpointDiscoveryService.class.getName());

    @Inject
    SecretsManagerService secretsManagerService;

    private String eventServiceEndpointAddress;

    private String cardServiceEndpointAddress;

    private String certificateServiceEndpointAddress;

    public void setSecretsManagerService(SecretsManagerService secretsManagerService) {
        this.secretsManagerService = secretsManagerService;
    }

    public void obtainConfiguration(String connectorBaseUrl) throws IOException, ParserConfigurationException {
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        clientBuilder.sslContext(secretsManagerService.getSslContext());

        clientBuilder = clientBuilder.hostnameVerifier(new SSLUtilities.FakeHostnameVerifier());

        Invocation.Builder builder = clientBuilder.build()
                .target(connectorBaseUrl)
                .path("/connector.sds")
                .request();

        Invocation invocation = builder
                .buildGet();

        try {
            InputStream inputStream = invocation.invoke(InputStream.class);
            Document document = DocumentBuilderFactory.newDefaultInstance()
                    .newDocumentBuilder()
                    .parse(inputStream);

            Node serviceInformationNode = getNodeWithTag(document.getDocumentElement(), "ServiceInformation");

            if (serviceInformationNode == null) {
                throw new IllegalArgumentException("Could not find single 'ServiceInformation'-tag");
            }

            NodeList serviceNodeList = serviceInformationNode.getChildNodes();

            for (int i = 0, n = serviceNodeList.getLength(); i < n; ++i) {
                Node node = serviceNodeList.item(i);

                if (node.getNodeType() != 1) {
                    // ignore formatting related text nodes
                    continue;
                }

                if (!node.hasAttributes() || node.getAttributes().getNamedItem("Name") == null) {
                    break;
                }

                switch (node.getAttributes().getNamedItem("Name").getTextContent()) {
                    case "EventService": {
                        eventServiceEndpointAddress = getEndpoint(node);
                        break;
                    }
                    case "CardService":{
                        cardServiceEndpointAddress = getEndpoint(node);
                        break;
                    }
                    case "CertificateService":{
                        certificateServiceEndpointAddress = getEndpoint(node);
                        break;
                    }
                }
            }

        } catch (ProcessingException | SAXException | IllegalArgumentException e) {
            log.log(Level.SEVERE, "Could not get or parse connector.sds", e);
        }
    }

    private String getEndpoint(Node serviceNode) {
        Node versionsNode = getNodeWithTag(serviceNode, "Versions");

        if (versionsNode == null) {
            throw new IllegalArgumentException("No version tags found");
        }
        NodeList versionNodes = versionsNode.getChildNodes();
        String location = "";
        for (int i = 0, n = versionNodes.getLength(); i < n; ++i) {
            Node versionNode = versionNodes.item(i);

            Node endpointNode = getNodeWithTag(versionNode, "EndpointTLS");

            if (endpointNode == null || !endpointNode.hasAttributes()
                    || endpointNode.getAttributes().getNamedItem("Location") == null) {
                continue;
            }

            location = endpointNode.getAttributes().getNamedItem("Location").getTextContent();

        }

        return location;
    }

    private Node getNodeWithTag(Node node, String tagName) {
        NodeList nodeList = node.getChildNodes();

        for (int i = 0, n = nodeList.getLength(); i < n; ++i) {
            Node childNode = nodeList.item(i);

            // ignore namespace entirely
            if (tagName.equals(childNode.getNodeName()) || childNode.getNodeName().endsWith(":" + tagName)) {
                return childNode;
            }
        }
        return null;
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
}
