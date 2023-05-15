package health.medunited.architecture.entities;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

import health.medunited.architecture.odata.annotations.ODataCacheControl;

@Entity
@NamedQuery(name="RuntimeConfig.findAll", query="SELECT r FROM RuntimeConfig r") 
@ODataCacheControl(maxAge = 5) // this odata entity set should be cached for 5 seconds
public class RuntimeConfig {

    @Id
    private String id = UUID.randomUUID().toString();

    private String userId;

    private String url;

    private String signPort;

    private String vzdPort;

    private String mandantId;

    private String clientSystemId;

    private String workplaceId;

    private boolean useSSL;

    @Column(columnDefinition = "TEXT", length = 65535)
    private String clientCertificate;

    private String clientCertificatePassword;

    public RuntimeConfig() {
    }

    public RuntimeConfig(String host, String signPort, String vzdPort, String mandantId, String clientSystemId, String workplaceId, String userId,
                         boolean useSSL, String clientCertificate, String clientCertificatePassword) {
        this.url = host;
        this.signPort = signPort;
        this.vzdPort = vzdPort;
        this.mandantId = mandantId;
        this.clientSystemId = clientSystemId;
        this.workplaceId = workplaceId;
        this.userId = userId;
        this.useSSL = useSSL;
        this.clientCertificate = clientCertificate;
        this.clientCertificatePassword = clientCertificatePassword;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSignPort() {
        return signPort;
    }

    public void setSignPort(String signPort) {
        this.signPort = signPort;
    }

    public String getVzdPort() {
        return vzdPort;
    }

    public void setVzdPort(String vzdPort) {
        this.vzdPort = vzdPort;
    }

    public String getMandantId() {
        return mandantId;
    }

    public void setMandantId(String mandantId) {
        this.mandantId = mandantId;
    }

    public String getClientSystemId() {
        return clientSystemId;
    }

    public void setClientSystemId(String clientSystemId) {
        this.clientSystemId = clientSystemId;
    }

    public String getWorkplaceId() {
        return workplaceId;
    }

    public void setWorkplaceId(String workplaceId) {
        this.workplaceId = workplaceId;
    }

    public boolean getUseSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public String getClientCertificate() {
        return clientCertificate;
    }

    public void setClientCertificate(String clientCertificate) {
        this.clientCertificate = clientCertificate;
    }

    public String getClientCertificatePassword() {
        return clientCertificatePassword;
    }

    public void setClientCertificatePassword(String clientCertificatePassword) {
        this.clientCertificatePassword = clientCertificatePassword;
    }
}
