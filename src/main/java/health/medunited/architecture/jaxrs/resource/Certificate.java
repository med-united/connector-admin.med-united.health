package health.medunited.architecture.jaxrs.resource;

import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.logging.Level;

@Path("certificate")
public class Certificate {

    /*
    @GET
    @Path("/getCertificate")
    public GetCardsResponse getCards() throws Throwable {
        try {
            GetCards getCards = new GetCards();
            getCards.setContext(copyValuesFromProxyIntoContextType(contextType));
            return eventServicePortType.getCards(getCards);
        } catch(Throwable t) {
            log.log(Level.WARNING, "Could not get cards", t);
            throw t;
        }
    }

    */

}
