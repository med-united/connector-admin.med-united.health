package health.medunited.architecture.jaxrs.resource;

import static health.medunited.architecture.provider.ContextTypeProducer.copyValuesFromProxyIntoContextType;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

import com.google.gson.Gson;
import de.gematik.ws.conn.cardservice.v8.CardInfoType;
import de.gematik.ws.conn.cardservice.v8.GetPinStatusResponse;
import de.gematik.ws.conn.cardservice.v8.PinStatusEnum;
import de.gematik.ws.conn.cardservice.wsdl.v8.CardServicePortType;
import de.gematik.ws.conn.cardservicecommon.v2.CardTypeType;
import de.gematik.ws.conn.cardservicecommon.v2.PinResultEnum;
import de.gematik.ws.conn.certificateservice.v6.ReadCardCertificate;
import de.gematik.ws.conn.certificateservice.v6.ReadCardCertificateResponse;
import de.gematik.ws.conn.certificateservice.v6.VerifyCertificateResponse;
import de.gematik.ws.conn.certificateservicecommon.v2.CertRefEnum;
import de.gematik.ws.conn.certificateservicecommon.v2.X509DataInfoListType;
import de.gematik.ws.conn.connectorcommon.v5.Status;
import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.cardservice.wsdl.v8.FaultMessage;
import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;
import health.medunited.architecture.jaxrs.resource.model.PINStatus;
import health.medunited.architecture.jaxrs.resource.model.VerifyAllEntry;

@RequestScoped
@Path("card")
public class Card {

    private static final Logger log = Logger.getLogger(Card.class.getName());

    @Context
    HttpServletRequest httpServletRequest;

    @Inject
    EventServicePortType eventServicePortType;

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

    @GET
    @Path("/PINStatus")
    public List<PINStatus> AllPinStatus() throws Throwable {
        GetCards getCards = new GetCards();
        getCards.setContext(copyValuesFromProxyIntoContextType(contextType));

        GetCardsResponse getCardResoponse = eventServicePortType.getCards(getCards);
        List<PINStatus> result = getCardResoponse.getCards().getCard().stream().map(this::getPINStatus).collect(Collectors.toList());
        return result;
    }

    private PINStatus getPINStatus(CardInfoType cardInfoType) {

        PINStatus pinStatus = new PINStatus();
        pinStatus.setCardInfoType(cardInfoType);

        String pinType = "";

        Holder<Status> status = new Holder<>();
        Holder<PinStatusEnum> pinStatusEnum = new Holder<>();
        Holder<BigInteger> leftTries = new Holder<>();

        if (cardInfoType.getCardType().equals(CardTypeType.SMC_B)) {
            pinType = "PIN.SMC";
        }
        else if(cardInfoType.getCardType().equals(CardTypeType.HBA)){
            pinType = "PIN.CH";
        }

        try {
            cardServicePortType.getPinStatus(
                    copyValuesFromProxyIntoContextType(contextType),
                    cardInfoType.getCardHandle(),
                    pinType,
                    status,
                    pinStatusEnum,
                    leftTries);

            pinStatus.setStatus(pinStatusEnum.value.toString());

        }  catch (FaultMessage e) {
            pinStatus.setStatus("NOT AVAILABLE");
            log.log(Level.SEVERE, e.getMessage());
        }

        return pinStatus;
    }


}
