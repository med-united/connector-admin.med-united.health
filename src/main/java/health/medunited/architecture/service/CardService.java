package health.medunited.architecture.service;

import health.medunited.architecture.scope.ConnectorScoped;

@ConnectorScoped
public class CardService {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(CardService.class.getName());

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void test() {
        LOG.info(url);
    }
}
