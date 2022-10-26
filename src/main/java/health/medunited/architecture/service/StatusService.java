package health.medunited.architecture.service;

import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;
import de.gematik.ws.conn.eventservice.wsdl.v7.FaultMessage;
import health.medunited.architecture.model.RuntimeConfig;
import health.medunited.architecture.provider.AbstractConnectorServicesProvider;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class StatusService {

    @Inject
    AbstractConnectorServicesProvider connectorServicesProvider;

    public void getStatus(RuntimeConfig runtimeConfig) throws FaultMessage {

        GetCards parameter = new GetCards();
        parameter.setContext(createContextType(runtimeConfig));

        EventServicePortType service = connectorServicesProvider.getEventServicePortType();

        GetCardsResponse r = service.getCards(parameter);
    }

    private ContextType createContextType(RuntimeConfig runtimeConfig) {
        ContextType contextType = new ContextType();
        contextType.setClientSystemId(runtimeConfig.getClientSystemId());
        contextType.setMandantId(runtimeConfig.getMandantId());
        contextType.setWorkplaceId(runtimeConfig.getWorkplaceId());
        contextType.setUserId(runtimeConfig.getUserId());
        return contextType;
    }


}
