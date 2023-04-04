package health.medunited.architecture.provider;

import de.gematik.ws._int.version.productinformation.v1.ProductTypeInformation;
import health.medunited.architecture.service.common.security.SecretsManagerService;
import health.medunited.architecture.service.productinformation.ProductInformationDiscoveryService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequestScoped
public class ConnectorProductInformationProducer {

    private static final Logger log = Logger.getLogger(ConnectorProductInformationProducer.class.getName());

    @Inject
    SecretsManagerService secretsManagerService;

    @Inject
    HttpServletRequest httpServletRequest;

    @Inject
    ProductInformationDiscoveryService productInformationDiscoveryService;

    private ProductTypeInformation connectorVersion;

    public void setSecretsManagerService(SecretsManagerService secretsManagerService) {
        this.secretsManagerService = secretsManagerService;
    }

    public void setProductInformationDiscoveryService(ProductInformationDiscoveryService productInformationDiscoveryService) {
        this.productInformationDiscoveryService = productInformationDiscoveryService;
    }

    @PostConstruct
    public void initializeProductInformation() {
        initializeProductInformation(false);
    }

    private void initializeProductInformation(boolean throwEndpointException) {
        try {
            productInformationDiscoveryService.obtainConfiguration(httpServletRequest.getHeader("x-host"));
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        initializeConnectorVersion();
    }

    public void initializeConnectorVersion() {
        connectorVersion = productInformationDiscoveryService.getConnectorVersion();
    }

    @Produces
    public ProductTypeInformation getConnectorVersion() {
        return this.connectorVersion;
    }
}
