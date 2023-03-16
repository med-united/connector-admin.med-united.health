package health.medunited.architecture.jaxrs.resource.model;

import de.gematik.ws.conn.cardservice.v8.CardInfoType;
import de.gematik.ws.conn.cardservice.v8.GetPinStatusResponse;

public class PINStatus {

    CardInfoType cardInfoType;

    String status;

    public void setCardInfoType(CardInfoType cardInfoType) {
        this.cardInfoType = cardInfoType;
    }

    public void setStatus(String status){
        this.status = status;
    }
}
