package health.medunited.architecture.context;

import de.gematik.ws.conn.connectorcontext.v2.ContextType;

public class ConnectorScopeContext {

    private String url;

    private ContextType contextType;

    private String sslCertificate;

    private String sslCertificatePassword;

    public ConnectorScopeContext(String url, ContextType contextType, String sslCertificate, String sslCertificatePassword) {
        this.url = url;
        this.contextType = contextType;
        this.sslCertificate = sslCertificate;
        this.sslCertificatePassword = sslCertificatePassword;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ContextType getContextType() {
        return contextType;
    }

    public void setContextType(ContextType contextType) {
        this.contextType = contextType;
    }

    public String getSslCertificate() {
        return sslCertificate;
    }

    public void setSslCertificate(String sslCertificate) {
        this.sslCertificate = sslCertificate;
    }

    public String getSslCertificatePassword() {
        return sslCertificatePassword;
    }

    public void setSslCertificatePassword(String sslCertificatePassword) {
        this.sslCertificatePassword = sslCertificatePassword;
    }
}
