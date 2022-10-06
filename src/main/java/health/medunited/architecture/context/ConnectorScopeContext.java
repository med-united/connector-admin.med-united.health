package health.medunited.architecture.context;

public class ConnectorScopeContext {

    private String url;

    private String terminalId;

    public ConnectorScopeContext(String url, String terminalId) {
        this.url = url;
        this.terminalId = terminalId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }
}
