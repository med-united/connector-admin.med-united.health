package health.medunited.architecture.jaxrs.resource;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.eventservice.v7.GetCardTerminals;
import de.gematik.ws.conn.eventservice.v7.GetCardTerminalsResponse;
import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;

@RequestScoped
@Path("event")
public class Event {

    private static Logger log = Logger.getLogger(Event.class.getName());

    @Context
    HttpServletRequest httpServletRequest;

    @Inject
    EventServicePortType eventServicePortType;

    @Inject
    ContextType contextType;

    @GET
    @Path("/get-cards")
    public GetCardsResponse GetCards() throws Throwable {
        try {
            GetCards getCards = new GetCards();
            getCards.setContext(copyValuesFromProxyIntoContextType(contextType));
            GetCardsResponse response = eventServicePortType.getCards(getCards);
            return response;
        } catch(Throwable t) {
            log.log(Level.WARNING, "Could not get cards", t);
            throw t;
        }
    }

    private static ContextType copyValuesFromProxyIntoContextType(ContextType contextType) {
        ContextType context = new ContextType();
        context.setMandantId(contextType.getMandantId());
        context.setWorkplaceId(contextType.getWorkplaceId());
        context.setClientSystemId(contextType.getClientSystemId());
        context.setUserId(contextType.getUserId());
        return context;
    }

    @GET
    @Path("/get-card-terminals")
    public GetCardTerminalsResponse GetCardTerminals() throws Throwable {
        try {   
            GetCardTerminals getCardTerminals = new GetCardTerminals();
            getCardTerminals.setContext(copyValuesFromProxyIntoContextType(contextType));
            GetCardTerminalsResponse response = eventServicePortType.getCardTerminals(getCardTerminals);
            return response;
        } catch(Throwable t) {
            log.log(Level.WARNING, "Could not get card terminals", t);
            throw t;
        }
    }

}
