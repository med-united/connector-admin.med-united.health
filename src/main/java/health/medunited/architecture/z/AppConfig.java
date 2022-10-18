package health.medunited.architecture.z;

import javax.enterprise.context.ApplicationScoped;
import javax.resource.spi.ConfigProperty;
import java.util.Optional;

@ApplicationScoped
public class AppConfig {

    @ConfigProperty()
    String directoryWatcherDir;

    @ConfigProperty()
    String prescriptionServiceURL;

    @ConfigProperty()
    String verifyHostname;

    @ConfigProperty()
    String idpClientId;

    @ConfigProperty()
    String idpAuthRequestRedirectURL;

    @ConfigProperty()
    String idpBaseURL;

    @ConfigProperty()
    boolean enableVau;

    @ConfigProperty()
    boolean enableBatchSign;

    @ConfigProperty()
    boolean includeRevocationInfo;

    @ConfigProperty()
    boolean writeSignatureFile;

    @ConfigProperty()
    String userAgent;

    @ConfigProperty()
    String connectorCrypt;

    @ConfigProperty()
    boolean xmlBundleDirectProcess;

    @ConfigProperty()
    Optional<String> certAuthStoreFile;

    @ConfigProperty()
    Optional<String> certAuthStoreFilePassword;

    public String getDirectoryWatcherDir() {
        return directoryWatcherDir;
    }

    public String getPrescriptionServiceURL() {
        return prescriptionServiceURL;
    }

    public String getVerifyHostname() {
        return verifyHostname;
    }

    public String getIdpClientId() {
        return idpClientId;
    }

    public String getIdpAuthRequestRedirectURL() {
        return idpAuthRequestRedirectURL;
    }

    public String getIdpBaseURL() {
        return idpBaseURL;
    }

    public boolean vauEnabled() {
        return enableVau;
    }

    public boolean enableBatchSign() {
        return enableBatchSign;
    }

    public boolean includeRevocationInfoEnabled() {
        return includeRevocationInfo;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getConnectorCrypt() {
        return connectorCrypt;
    }

    public Optional<String> getCertAuthStoreFile() {
        return certAuthStoreFile;
    }

    public Optional<String> getCertAuthStoreFilePassword() {
        return certAuthStoreFilePassword;
    }

    public boolean isWriteSignatureFile() {
        return this.writeSignatureFile;
    }

    public boolean getWriteSignatureFile() {
        return this.writeSignatureFile;
    }

    public boolean getXmlBundleDirectProcess() {
        return this.xmlBundleDirectProcess;
    }

}
