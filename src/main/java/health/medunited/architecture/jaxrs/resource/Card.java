package health.medunited.architecture.jaxrs.resource;

import static health.medunited.architecture.provider.ContextTypeProducer.copyValuesFromProxyIntoContextType;

import java.math.BigInteger;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;

import javax.xml.ws.Holder;


import de.gematik.ws.conn.cardservice.v8.CardInfoType;
import de.gematik.ws.conn.cardservice.v8.PinStatusEnum;
import de.gematik.ws.conn.cardservice.wsdl.v8.CardServicePortType;
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
        String[] pinTypes = {"PIN.CH", "PIN.QES"};
        for (PINStatus pinStatus : result) {
            if ((!pinStatus.getType().equals("SMC_KT")) && (!pinStatus.getType().equals("KVK"))) {
                if(pinStatus.getType().equals("HBA")){
                    for(int i = 0; i< 2; i++){
                        int pos = pinStatus.getStatus().indexOf("/");
                        String statusCH = pinStatus.getStatus().substring(0,pos);
                        String statusQES = pinStatus.getStatus().substring(pos+1);
                        String[] status = {statusCH, statusQES};
                        JsonObject value = Json.createObjectBuilder()
                                .add("cardHandle", pinStatus.getHandle())
                                .add("cardType", pinStatus.getType())
                                .add("pinType", pinTypes[i])
                                .add("status", status[i]).build();
                        items.add(value);
                    }
                }
                else {
                    JsonObject value = Json.createObjectBuilder()
                            .add("cardHandle", pinStatus.getHandle())
                            .add("cardType", pinStatus.getType())
                            .add("pinType", pinStatus.getPINType())
                            .add("status", pinStatus.getStatus()).build();
                    items.add(value);
                }
            }
        }
        return items;
    }

    private PINStatus getPINStatus(CardInfoType cardInfoType) {

        String pinType = "";
        PINStatus pinStatus = new PINStatus();
        Holder<Status> status = new Holder<>();
        Holder<PinStatusEnum> pinStatusEnum = new Holder<>();
        Holder<BigInteger> leftTries = new Holder<>();

        pinStatus.setCardInfoType(cardInfoType);
        pinStatus.setHandle(cardInfoType.getCardHandle());
        pinStatus.setType(cardInfoType.getCardType().toString());

        if (pinStatus.getType().equals("HBA")) {
                pinStatus.setPINType("PIN.CH");
                try {
                    cardServicePortType.getPinStatus(
                            copyValuesFromProxyIntoContextType(contextType),
                            cardInfoType.getCardHandle(),
                            pinStatus.getPINType(),
                            status,
                            pinStatusEnum,
                            leftTries
                    );
                    String statusCH = pinStatusEnum.value.toString();
                    pinStatus.setPINType("PIN.QES");
                    cardServicePortType.getPinStatus(
                            copyValuesFromProxyIntoContextType(contextType),
                            cardInfoType.getCardHandle(),
                            pinStatus.getPINType(),
                            status,
                            pinStatusEnum,
                            leftTries
                    );
                    String statusQES = pinStatusEnum.value.toString();
                    pinStatus.setStatus(statusCH + "/" + statusQES);
                } catch (FaultMessage e) {
                    log.log(Level.SEVERE, e.getMessage());
                }
        } else {
            if(pinStatus.getType().equals("SMC_B")){
                pinType = "PIN.SMC";
            } else if (pinStatus.getType().equals("EGK")) {
                pinType = "PIN.CH";
            }
            pinStatus.setPINType(pinType);
            try {
                cardServicePortType.getPinStatus(
                        copyValuesFromProxyIntoContextType(contextType),
                        cardInfoType.getCardHandle(),
                        pinStatus.getPINType(),
                        status,
                        pinStatusEnum,
                        leftTries
                );
                pinStatus.setStatus(pinStatusEnum.value.toString());
            } catch (FaultMessage e) {
                log.log(Level.SEVERE, e.getMessage());
            }
        }
        return pinStatus;
    }

}
