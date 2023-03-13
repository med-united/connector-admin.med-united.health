package health.medunited.architecture.jaxrs.resource.model;

import java.util.ArrayList;
import java.util.List;

import de.gematik.ws.conn.cardservice.v8.CardInfoType;
import de.gematik.ws.conn.certificateservice.v6.ReadCardCertificateResponse;
import de.gematik.ws.conn.certificateservice.v6.VerifyCertificateResponse;

public class VerifyAllEntry {
    CardInfoType cardInfoType;
    ReadCardCertificateResponse readCardCertificateResponse;
    List<VerifyCertificateResponse> verifyCertificateResponse = new ArrayList<>();

    public CardInfoType getCardInfoType() {
        return this.cardInfoType;
    }

    public void setCardInfoType(CardInfoType cardInfoType) {
        this.cardInfoType = cardInfoType;
    }

    public ReadCardCertificateResponse getReadCardCertificateResponse() {
        return this.readCardCertificateResponse;
    }

    public void setReadCardCertificateResponse(ReadCardCertificateResponse readCardCertificateResponse) {
        this.readCardCertificateResponse = readCardCertificateResponse;
    }

    public List<VerifyCertificateResponse> getVerifyCertificateResponse() {
        return this.verifyCertificateResponse;
    }

    public void setVerifyCertificateResponse(List<VerifyCertificateResponse> verifyCertificateResponse) {
        this.verifyCertificateResponse = verifyCertificateResponse;
    }
    
}
