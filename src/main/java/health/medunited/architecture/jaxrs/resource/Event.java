package health.medunited.architecture.jaxrs.resource;

import static health.medunited.architecture.provider.ContextTypeProducer.copyValuesFromProxyIntoContextType;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.eventservice.v7.GetCardTerminals;
import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;

@RequestScoped
@Path("event")
public class Event {

    private static final Logger log = Logger.getLogger(Event.class.getName());

    @Context
    HttpServletRequest httpServletRequest;

    @Inject
    EventServicePortType eventServicePortType;

    @Inject
    ContextType contextType;

    @GET
    @Path("/get-cards")
    public Response getCards() {
        try {
            GetCards getCards = new GetCards();
            getCards.setContext(copyValuesFromProxyIntoContextType(contextType));
            return Response.status(Response.Status.OK)
            .entity(eventServicePortType.getCards(getCards))
            .type(MediaType.APPLICATION_JSON_TYPE)
            .build();
        } catch(Throwable t) {
            log.log(Level.WARNING, "Could not get cards", t);
            return Response.status(Response.Status.NOT_FOUND).entity(new GetCardsResponse()).build();
        }
    }

    @GET
    @Path("/get-card-terminals")
    public Response getCardTerminals() {
        try {   
            GetCardTerminals getCardTerminals = new GetCardTerminals();
            getCardTerminals.setContext(copyValuesFromProxyIntoContextType(contextType));
            return Response.status(Response.Status.OK)
            .entity(eventServicePortType.getCardTerminals(getCardTerminals))
            .type(MediaType.APPLICATION_JSON_TYPE)
            .build();
        } catch(Throwable t) {
            log.log(Level.WARNING, "Could not get card terminals", t);
            return Response.status(Response.Status.NOT_FOUND).entity(new GetCardsResponse()).build();
        }
    }

}
