package health.medunited.architecture.jaxrs.resource.model;

import de.gematik.ws.conn.cardservice.v8.CardInfoType;
import de.gematik.ws.conn.cardservice.v8.GetPinStatusResponse;

public class PINStatus {

    CardInfoType cardInfoType;

    String status;

    String handle;

    String type;

    public void setCardInfoType(CardInfoType cardInfoType) {
        this.cardInfoType = cardInfoType;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){return this.status;}

    public void setHandle(String handle){ this.handle = handle;}

    public String getHandle(){return this.handle;}

    public void setType(String type) { this.type = type;}

    public String getType(){return this.type;}
}
