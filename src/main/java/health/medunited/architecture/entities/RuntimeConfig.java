package health.medunited.architecture.entities;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

import health.medunited.architecture.odata.annotations.ODataCacheControl;

@Entity
@NamedQuery(name = "RuntimeConfig.findAll", query = "SELECT r FROM RuntimeConfig r")
@ODataCacheControl(maxAge = 5) // this odata entity set should be cached for 5 seconds
public class RuntimeConfig {

  @Id
  private String id = UUID.randomUUID().toString();

  private String userId;

  private String url;

  private String connectorBrand;

  private String mandantId;

  private String clientSystemId;

  private String workplaceId;

  private String username;

  private String password;

  private boolean useCertificateAuth;

  @Column(columnDefinition = "TEXT", length = 65535)
  private String clientCertificate;

  private String clientCertificatePassword;

  private boolean useBasicAuth;

  private String basicAuthUsername;

  private String basicAuthPassword;

  public RuntimeConfig() {}

  public RuntimeConfig(String userId, String url, String connectorBrand, String mandantId,
      String clientSystemId, String workplaceId, String username, String password,
      boolean useCertificateAuth, String clientCertificate, String clientCertificatePassword,
      boolean useBasicAuth, String basicAuthUsername, String basicAuthPassword) {
    this.userId = userId;
    this.url = url;
    this.connectorBrand = connectorBrand;
    this.mandantId = mandantId;
    this.clientSystemId = clientSystemId;
    this.workplaceId = workplaceId;
    this.username = username;
    this.password = password;
    this.useCertificateAuth = useCertificateAuth;
    this.clientCertificate = clientCertificate;
    this.clientCertificatePassword = clientCertificatePassword;
    this.useBasicAuth = useBasicAuth;
    this.basicAuthUsername = basicAuthUsername;
    this.basicAuthPassword = basicAuthPassword;
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

  public String getConnectorBrand() {
    return connectorBrand;
  }

  public void setConnectorBrand(String connectorBrand) {
    this.connectorBrand = connectorBrand;
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

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean getUseCertificateAuth() {
    return useCertificateAuth;
  }

  public void setUseCertificateAuth(boolean useCertificateAuth) {
    this.useCertificateAuth = useCertificateAuth;
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

  public boolean getUseBasicAuth() {
    return useBasicAuth;
  }

  public void setUseBasicAuth(boolean useBasicAuth) {
    this.useBasicAuth = useBasicAuth;
  }

  public String getBasicAuthUsername() {
    return basicAuthUsername;
  }

  public void setBasicAuthUsername(String basicAuthUsername) {
    this.basicAuthUsername = basicAuthUsername;
  }

  public String getBasicAuthPassword() {
    return basicAuthPassword;
  }

  public void setBasicAuthPassword(String basicAuthPassword) {
    this.basicAuthPassword = basicAuthPassword;
  }
}
