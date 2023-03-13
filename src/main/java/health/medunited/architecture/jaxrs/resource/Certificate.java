package health.medunited.architecture.jaxrs.resource;

import static health.medunited.architecture.provider.ContextTypeProducer.copyValuesFromProxyIntoContextType;

import java.security.cert.X509Certificate;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

import de.gematik.ws.conn.cardservice.v8.CardInfoType;
import de.gematik.ws.conn.cardservicecommon.v2.CardTypeType;
import de.gematik.ws.conn.certificateservice.v6.ReadCardCertificate;
import de.gematik.ws.conn.certificateservice.v6.ReadCardCertificate.CertRefList;
import de.gematik.ws.conn.certificateservice.v6.ReadCardCertificateResponse;
import de.gematik.ws.conn.certificateservice.v6.VerifyCertificateResponse;
import de.gematik.ws.conn.certificateservice.wsdl.v6.CertificateServicePortType;
import de.gematik.ws.conn.certificateservice.wsdl.v6.FaultMessage;
import de.gematik.ws.conn.certificateservicecommon.v2.CertRefEnum;
import de.gematik.ws.conn.certificateservicecommon.v2.X509DataInfoListType;
import de.gematik.ws.conn.certificateservicecommon.v2.X509DataInfoListType.X509DataInfo;
import de.gematik.ws.conn.connectorcommon.v5.Status;
import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;
import health.medunited.architecture.jaxrs.resource.model.VerifyAllEntry;

@Path("certificate")
public class Certificate {

    private static final Logger log = Logger.getLogger(Certificate.class.getName());


    X509Certificate x509Certificate;

    @Context
    HttpServletRequest httpServletRequest;

    @Inject
    EventServicePortType eventServicePortType;

    @Inject
    CertificateServicePortType certificateServicePortType;

    @Inject
    ContextType contextType;

    @GET
    @Path("/verifyAll")
    public List<VerifyAllEntry> verifyAll() throws Throwable {
        GetCards getCards = new GetCards();
        getCards.setContext(copyValuesFromProxyIntoContextType(contextType));

        GetCardsResponse getCardResoponse = eventServicePortType.getCards(getCards);

        return getCardResoponse.getCards().getCard().stream().map(this::card2VerifyAllEntry).collect(Collectors.toList());
    }

    private VerifyAllEntry card2VerifyAllEntry(CardInfoType cardInfoType) {
        VerifyAllEntry verifyAllEntry = new VerifyAllEntry();
        verifyAllEntry.setCardInfoType(cardInfoType);
        // Certificates from Card Type
        // eHBA (C.AUT, C.QES)
        CertRefList availableCertificates = new ReadCardCertificate.CertRefList();
        if (cardInfoType.getCardType().equals(CardTypeType.HBA)) {
            availableCertificates.getCertRef().add(CertRefEnum.C_ENC);
            availableCertificates.getCertRef().add(CertRefEnum.C_AUT);
            availableCertificates.getCertRef().add(CertRefEnum.C_QES);
        } else if (cardInfoType.getCardType().equals(CardTypeType.SMC_B)) {
            availableCertificates.getCertRef().add(CertRefEnum.C_ENC);
            availableCertificates.getCertRef().add(CertRefEnum.C_AUT);
            availableCertificates.getCertRef().add(CertRefEnum.C_SIG);
        } else if (cardInfoType.getCardType().equals(CardTypeType.SMC_KT)) {
            // Nothing available
            return verifyAllEntry;
        } else if (cardInfoType.getCardType().equals(CardTypeType.EGK)) {
            // Zugriff auf eGK nicht gestattet 
            return verifyAllEntry;
        }

        ReadCardCertificate readCardCertificate = new ReadCardCertificate();
        readCardCertificate.setContext(copyValuesFromProxyIntoContextType(contextType));

        Holder<Status> status = new Holder<>();
        Holder<X509DataInfoListType> certList = new Holder<>();

        try {
            certificateServicePortType.readCardCertificate(
                    cardInfoType.getCardHandle(),
                    copyValuesFromProxyIntoContextType(contextType),
                    availableCertificates,
                    status,
                    certList
            );
            ReadCardCertificateResponse readCardCertificateResponse = new ReadCardCertificateResponse();

            readCardCertificateResponse.setStatus(status.value);
            readCardCertificateResponse.setX509DataInfoList(certList.value);
            verifyAllEntry.setReadCardCertificateResponse(readCardCertificateResponse);

            for (X509DataInfo x509DataInfo : readCardCertificateResponse.getX509DataInfoList().getX509DataInfo()) {
                byte[] encodedCertificate = x509DataInfo.getX509Data().getX509Certificate();
                XMLGregorianCalendar now = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(TimeZone.getTimeZone("UTC")));
                Holder<Status> status2 = new Holder<>();
                Holder<VerifyCertificateResponse.VerificationStatus> verificationStatus = new Holder<>();
                Holder<VerifyCertificateResponse.RoleList> roleList = new Holder<>();

                certificateServicePortType.verifyCertificate(copyValuesFromProxyIntoContextType(contextType), encodedCertificate, now, status, verificationStatus, roleList);

                VerifyCertificateResponse verifyCertificateResponse = new VerifyCertificateResponse();
                verifyCertificateResponse.setStatus(status2.value);
                verifyCertificateResponse.setVerificationStatus(verificationStatus.value);
                verifyCertificateResponse.setRoleList(roleList.value);
                verifyAllEntry.getVerifyCertificateResponse().add(verifyCertificateResponse);

            }
        } catch (FaultMessage | DatatypeConfigurationException e) {
            log.log(Level.SEVERE, "Could not read certificate", e);
        }
        return verifyAllEntry;
    }
}
