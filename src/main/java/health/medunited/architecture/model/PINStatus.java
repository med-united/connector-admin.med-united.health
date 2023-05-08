package health.medunited.architecture.model;

import de.gematik.ws.conn.cardservice.v8.CardInfoType;

public class PINStatus {

    CardInfoType cardInfoType;

    String status;

    String handle;

    String type;

    String pinType;

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

    public void setPINType(String pinType) { this.pinType = pinType;}

    public String getPINType(){return this.pinType;}
}
