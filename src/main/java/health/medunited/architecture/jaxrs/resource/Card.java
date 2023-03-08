package health.medunited.architecture.jaxrs.resource;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.xml.ws.Holder;

import de.gematik.ws.conn.cardservicecommon.v2.PinResultEnum;
import de.gematik.ws.conn.connectorcommon.v5.Status;
import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.eventservice.v7.GetCardTerminals;
import de.gematik.ws.conn.eventservice.v7.GetCardTerminalsResponse;
import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;
import de.gematik.ws.conn.cardservice.wsdl.v8.CardServicePortType;

@RequestScoped
@Path("cards")
public class Card {

    private static final Logger log = Logger.getLogger(Event.class.getName());

    @Context
    HttpServletRequest httpServletRequest;

    @Inject
    EventServicePortType eventServicePortType;

    @Inject
    CardServicePortType cardServicePortType;

    String tempCardHandle = "HBA-67";

    @Inject
    ContextType contextType;

    @GET
    @Path("/changePin")
    public String changePin()  {
        System.out.println("CHANGING PIN");
        Holder<Status> status = new Holder<>();
        Holder<PinResultEnum> pinResultEnum = new Holder<>();
        Holder<BigInteger> leftTries = new Holder<>();

        ContextType ct2 = copyValuesFromProxyIntoContextType(contextType);
        try {
            this.cardServicePortType.changePin(ct2, tempCardHandle, "PIN.QES", status, pinResultEnum, leftTries);
            return "PIN Change triggered:" +status.value.toString();
        } catch (de.gematik.ws.conn.cardservice.wsdl.v8.FaultMessage e) {
            e.printStackTrace();
        }

        return "changePin function";
    }


    private static ContextType copyValuesFromProxyIntoContextType(ContextType contextType) {
        ContextType context = new ContextType();
        context.setMandantId(contextType.getMandantId());
        context.setWorkplaceId(contextType.getWorkplaceId());
        context.setClientSystemId(contextType.getClientSystemId());
        context.setUserId(contextType.getUserId());
        return context;
    }

}
