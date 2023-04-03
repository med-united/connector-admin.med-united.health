package health.medunited.architecture.service.productinformation;

import de.gematik.ws._int.version.productinformation.v1.ProductTypeInformation;
import health.medunited.architecture.service.common.security.SecretsManagerService;
import health.medunited.architecture.service.endpoint.SSLUtilities;
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
public class ProductInformationDiscoveryService {

    private static final Logger log = Logger.getLogger(ProductInformationDiscoveryService.class.getName());

    @Inject
    SecretsManagerService secretsManagerService;

    private ProductTypeInformation connectorVersion;

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

            connectorVersion = getConnectorVersion(document);

        } catch (ProcessingException | SAXException | IllegalArgumentException e) {
            log.log(Level.SEVERE, "Could not get or parse connector.sds", e);
        }
    }

    public ProductTypeInformation getConnectorVersion(Document document) {
        Node productInformationNode = getNodeWithTag(document.getDocumentElement(), "ProductInformation");
        if (productInformationNode == null) {
            throw new IllegalArgumentException("Could not find single 'ProductInformation'-tag");
        }
        Node productTypeInformationNode = getNodeWithTag(productInformationNode, "ProductTypeInformation");
        if (productTypeInformationNode == null) {
            throw new IllegalArgumentException("Could not find single 'ProductTypeInformation'-tag");
        }
        Node productTypeVersionNode = getNodeWithTag(productTypeInformationNode, "ProductTypeVersion");
        if (productTypeVersionNode == null) {
            throw new IllegalArgumentException("Could not find single 'ProductTypeVersion'-tag");
        }

        ProductTypeInformation productTypeInformation = new ProductTypeInformation();
        productTypeInformation.setProductTypeVersion(productTypeVersionNode.getTextContent());
        return productTypeInformation;
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

    public ProductTypeInformation getConnectorVersion() {
        return connectorVersion;
    }
}
