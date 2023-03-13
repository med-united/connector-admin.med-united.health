package health.medunited.architecture.jaxrs.resource;

import static health.medunited.architecture.provider.ContextTypeProducer.copyValuesFromProxyIntoContextType;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.xml.ws.Holder;

import de.gematik.ws.conn.cardservice.wsdl.v8.CardServicePortType;
import de.gematik.ws.conn.cardservicecommon.v2.PinResultEnum;
import de.gematik.ws.conn.connectorcommon.v5.Status;
import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.cardservice.wsdl.v8.FaultMessage;

@RequestScoped
@Path("card")
public class Card {

    private static final Logger log = Logger.getLogger(Card.class.getName());

    @Context
    HttpServletRequest httpServletRequest;

    @Inject
    CardServicePortType cardServicePortType;

    @Inject
    ContextType contextType;

    @GET
    @Path("/changePin")
    public String changePin(@QueryParam("cardHandle") String cardHandle, @QueryParam("pinType") String pinType) {

        log.info("CHANGING PIN");
        Holder<Status> status = new Holder<>();
        Holder<PinResultEnum> pinResultEnum = new Holder<>();
        Holder<BigInteger> leftTries = new Holder<>();
        try {
            cardServicePortType.changePin(copyValuesFromProxyIntoContextType(contextType), cardHandle, pinType, status, pinResultEnum, leftTries);
            return "PIN Change triggered:" + status.value.toString();
        } catch (FaultMessage e) {
            log.log(Level.SEVERE, e.getMessage());
        }

        return "Waiting for user input on the terminal...";
    }

    @GET
    @Path("/verifyPin")
    public String verifyPin(@QueryParam("cardHandle") String cardHandle, @QueryParam("pinType") String pinType) {

        log.info("VERIFYING PIN");
        Holder<Status> status = new Holder<>();
        Holder<PinResultEnum> pinResultEnum = new Holder<>();
        Holder<BigInteger> leftTries = new Holder<>();
        try {
            cardServicePortType.verifyPin(
                    copyValuesFromProxyIntoContextType(contextType), cardHandle, pinType, status, pinResultEnum, leftTries
            );
            return "PIN verification triggered:" + status.value.toString();
        } catch (FaultMessage e) {
            log.log(Level.SEVERE, e.getMessage());
        }

        return "Waiting for user input on the terminal for verification...";
    }

}
