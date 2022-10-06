package health.medunited.architecture.service;

import javax.enterprise.inject.Alternative;

@Alternative
public class CardService {

    private String url;

    private String terminalId;

    public CardService(String url, String terminalId) {
        this.url = url;
        this.terminalId = terminalId;
    }

    public CardService() {
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