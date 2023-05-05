package health.medunited.architecture.jaxrs.resource;

import static health.medunited.architecture.provider.ContextTypeProducer.copyValuesFromProxyIntoContextType;

import java.math.BigInteger;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import javax.xml.ws.Holder;


import de.gematik.ws.conn.cardservice.v8.CardInfoType;
import de.gematik.ws.conn.cardservice.v8.PinStatusEnum;
import de.gematik.ws.conn.cardservice.wsdl.v8.CardServicePortType;
import de.gematik.ws.conn.cardservicecommon.v2.CardTypeType;
import de.gematik.ws.conn.cardservicecommon.v2.PinResultEnum;
import de.gematik.ws.conn.connectorcommon.v5.Status;
import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.cardservice.wsdl.v8.FaultMessage;
import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;
import health.medunited.architecture.model.PINStatus;

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
    @Path("/pinStatus")
    @Produces("application/json")
    public Collection<JsonObject> allPinStatus() throws Throwable {
        GetCards getCards = new GetCards();
        getCards.setContext(copyValuesFromProxyIntoContextType(contextType));

        GetCardsResponse getCardResponse = eventServicePortType.getCards(getCards);
        List<PINStatus> result = getCardResponse.getCards().getCard().stream().map(this::getPINStatus).collect(Collectors.toList());
        Collection<JsonObject> items = new ArrayList<>();
        for (PINStatus pinStatus : result) {
            if (pinStatus.getType() != "SMC_KT" && pinStatus.getType() != "KVK") {
                    JsonObject value = Json.createObjectBuilder()
                            .add("cardHandle", pinStatus.getHandle())
                            .add("cardType", pinStatus.getType())
                            .add("status", pinStatus.getStatus()).build();
                    items.add(value);
            }
        }
        return items;
    }

    private PINStatus getPINStatus(CardInfoType cardInfoType) {

        PINStatus pinStatus = new PINStatus();
        pinStatus.setCardInfoType(cardInfoType);

        pinStatus.setHandle(cardInfoType.getCardHandle());
        pinStatus.setType(cardInfoType.getCardType().toString());

        String pinType = "";
        String pinType0 = "";
        String pinType1 = "";

        Holder<Status> status = new Holder<>();
        Holder<PinStatusEnum> pinStatusEnum = new Holder<>();
        Holder<BigInteger> leftTries = new Holder<>();

        if (cardInfoType.getCardType().equals(CardTypeType.SMC_B)) {
            pinType = "PIN.SMC";
        }
        else if (cardInfoType.getCardType().equals(CardTypeType.EGK)){
            pinType = "PIN.CH";
        }
        else if(cardInfoType.getCardType().equals(CardTypeType.HBA))
        {
            pinType0 = "PIN.CH";
            pinType1 = "PIN.QES";
        }
            if (pinStatus.getType() == "HBA") {
                    try{
                        cardServicePortType.getPinStatus(
                                copyValuesFromProxyIntoContextType(contextType),
                                cardInfoType.getCardHandle(),
                                pinType0,
                                status,
                                pinStatusEnum,
                                leftTries);
                        String statusCH = pinStatusEnum.value.toString();
                        cardServicePortType.getPinStatus(
                                copyValuesFromProxyIntoContextType(contextType),
                                cardInfoType.getCardHandle(),
                                pinType1,
                                status,
                                pinStatusEnum,
                                leftTries);
                        String statusQES = pinStatusEnum.value.toString();
                        pinStatus.setStatus("PIN.CH: " + statusCH + " PIN.QES: " + statusQES);
                    }
                    catch(FaultMessage e){
                        log.log(Level.SEVERE, e.getMessage());
                    }
            } else {
                try{
                    cardServicePortType.getPinStatus(
                            copyValuesFromProxyIntoContextType(contextType),
                            cardInfoType.getCardHandle(),
                            pinType,
                            status,
                            pinStatusEnum,
                            leftTries);

                    pinStatus.setStatus(pinStatusEnum.value.toString());
                }
                catch(FaultMessage e){
                    log.log(Level.SEVERE, e.getMessage());
                }
            }
            return pinStatus;
    }
}
