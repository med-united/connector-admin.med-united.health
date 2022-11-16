package health.medunited.architecture.model;

import javax.servlet.http.HttpServletRequest;

public class RuntimeConfig {

    private String url;

    private String mandantId;

    private String clientSystemId;

    private String workplaceId;

    private String userId;

    private String clientCertificate;

    private String clientCertificatePassword;

    public RuntimeConfig(HttpServletRequest httpServletRequest) {
        updateConfigurationsWithHttpServletRequest(httpServletRequest);
    }

    private void updateConfigurationsWithHttpServletRequest(HttpServletRequest httpServletRequest) {
        if(httpServletRequest == null) {
            return;
        }
        this.url = httpServletRequest.getHeader("X-Url");
        this.mandantId = httpServletRequest.getHeader("X-MandantId");
        this.clientSystemId = httpServletRequest.getHeader("X-ClientSystemId");
        this.workplaceId = httpServletRequest.getHeader("X-WorkplaceId");
        this.userId = httpServletRequest.getHeader("X-UserId");
        this.clientCertificate = httpServletRequest.getHeader("X-ClientCertificate");
        this.clientCertificatePassword = httpServletRequest.getHeader("X-ClientCertificatePassword");
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
