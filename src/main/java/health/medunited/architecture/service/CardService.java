package health.medunited.architecture.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

@ApplicationScoped
public class CardService {

    private String url;

    private String mandantId;

    private String clientSystemId;

    private String workplaceId;

    private String userId;

    public CardService(String url, String mandantId, String clientSystemId, String workplaceId, String userId) {
        this.url = url;
        this.mandantId = mandantId;
        this.clientSystemId = clientSystemId;
        this.workplaceId = workplaceId;
        this.userId = userId;
    }

    public CardService() {
    }

    public String getCardStatus() {
        return "OK" + url + mandantId + clientSystemId + workplaceId + userId;
    }


}

