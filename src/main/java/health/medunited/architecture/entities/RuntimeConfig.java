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

    private String mandantId;

    private String clientSystemId;

    private String username;

    private String password;

    private String workplaceId;

    private boolean useSSL;

    @Column(columnDefinition = "TEXT", length = 65535)
    private String clientCertificate;

    private String clientCertificatePassword;

    public RuntimeConfig() {
    }

    public RuntimeConfig(String host, String mandantId, String clientSystemId, String workplaceId, String userId,
                         boolean useSSL, String clientCertificate, String clientCertificatePassword) {
        this.url = host;
        this.mandantId = mandantId;
        this.clientSystemId = clientSystemId;
        this.workplaceId = workplaceId;
        this.username = username;
        this.password = password;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
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
