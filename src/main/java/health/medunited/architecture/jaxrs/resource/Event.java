package health.medunited.architecture.jaxrs.resource;

import static health.medunited.architecture.provider.ContextTypeProducer.copyValuesFromProxyIntoContextType;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
