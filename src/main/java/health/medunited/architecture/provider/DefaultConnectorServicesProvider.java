package health.medunited.architecture.provider;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;

@RequestScoped
public class DefaultConnectorServicesProvider extends AbstractConnectorServicesProvider {

    @PostConstruct
    void init() {
        initializeServices();
    }

    public void setSslCertificate(String sslCertificate, String sslCertificatePassword) {
        super.setSslCredentials(sslCertificate, sslCertificatePassword);
    }
}
