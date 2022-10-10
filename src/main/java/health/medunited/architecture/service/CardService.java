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
import health.medunited.architecture.provider.MultiConnectorServicesProvider;
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

    private MultiConnectorServicesProvider connectorServicesProvider;

    public CardService(String url, String mandantId, String clientSystemId, String workplaceId, String userId) {
        this.url = url;
        this.mandantId = mandantId;
        this.clientSystemId = clientSystemId;
        this.workplaceId = workplaceId;
        this.userId = userId;
    }

    public CardService() {
    }

    public void setConnectorServicesProvider (MultiConnectorServicesProvider connectorServicesProvider) {
        this.connectorServicesProvider = connectorServicesProvider;
    }

    public String getCardStatus() {
        return "OK" + url + mandantId + clientSystemId + workplaceId + userId;
    }

    public String getConnectorCardHandle(CardHandleType cardHandleType, ConnectorScopeContext connectorScopeContext)
            throws ConnectorCardsException {
        return getConnectorCardHandle(ch ->
                ch.getCardType().value().equalsIgnoreCase(
                        cardHandleType.getCardHandleType()), connectorScopeContext);
    }

    private String  getConnectorCardHandle(Predicate<? super CardInfoType> filter, ConnectorScopeContext connectorScopeContext)
            throws ConnectorCardsException {
        Optional<List<CardInfoType>> cardsInfoList = getConnectorCardsInfo(connectorScopeContext);
        String cardHandle = null;

        if (cardsInfoList.isPresent()) {
            Optional<CardInfoType> cardHndl =
                    cardsInfoList.get().stream().filter(filter).findFirst();
            if (cardHndl.isPresent()) {
                cardHandle = cardHndl.get().getCardHandle();
            } else {
                throw new ConnectorCardsException(String.format("No card handle found for card."));
            }
        }

        return cardHandle;
    }

    private Optional<List<CardInfoType>> getConnectorCardsInfo(ConnectorScopeContext connectorScopeContext) throws ConnectorCardsException {
        GetCardsResponse response = getConnectorCards(connectorScopeContext);
        List<CardInfoType> cardHandleTypeList = null;

        if (response != null) {
            Cards cards = response.getCards();
            cardHandleTypeList = cards.getCard();

            if (CollectionUtils.isEmpty(cardHandleTypeList)) {
                throw new ConnectorCardsException("Error. Did not receive and card handle data.");
            }
        }

        return Optional.ofNullable(cardHandleTypeList);
    }

    private GetCardsResponse getConnectorCards(ConnectorScopeContext connectorScopeContext) throws ConnectorCardsException {
        GetCards parameter = new GetCards();
        ContextType contextType = new ContextType();
        contextType.setMandantId(connectorScopeContext.getMandantId());
        contextType.setClientSystemId(connectorScopeContext.getClientSystemId());
        contextType.setWorkplaceId(connectorScopeContext.getWorkplaceId());
        contextType.setUserId(connectorScopeContext.getUserId());
        parameter.setContext(contextType);
        try {
            EventServicePortType eventServicePortType = connectorServicesProvider.getEventServicePortType(connectorScopeContext);
            return eventServicePortType.getCards(parameter);
        } catch (FaultMessage e) {
            throw new ConnectorCardsException("Error getting connector card handles.", e);
        }
    }

}

