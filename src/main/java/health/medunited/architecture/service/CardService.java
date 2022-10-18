package health.medunited.architecture.service;

import de.gematik.ws.conn.cardservice.v8.CardInfoType;
import de.gematik.ws.conn.cardservice.v8.Cards;
import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;
import de.gematik.ws.conn.eventservice.wsdl.v7.FaultMessage;
import health.medunited.architecture.context.ConnectorScopeContext;
import health.medunited.architecture.exception.connector.ConnectorCardsException;
import health.medunited.architecture.model.CardHandleType;
import org.apache.commons.collections4.CollectionUtils;

import javax.enterprise.inject.Alternative;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Alternative
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

