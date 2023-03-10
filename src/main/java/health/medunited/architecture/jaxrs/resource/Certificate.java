package health.medunited.architecture.jaxrs.resource;

import static health.medunited.architecture.provider.ContextTypeProducer.copyValuesFromProxyIntoContextType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import de.gematik.ws.conn.cardservice.v8.CardInfoType;
import de.gematik.ws.conn.cardservicecommon.v2.CardTypeType;
import de.gematik.ws.conn.certificateservice.v6.ReadCardCertificate;
import de.gematik.ws.conn.certificateservice.v6.ReadCardCertificate.CertRefList;
import de.gematik.ws.conn.certificateservice.v6.ReadCardCertificateResponse;
import de.gematik.ws.conn.certificateservice.v6.VerificationResultType;
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

    private static final Logger log = Logger.getLogger(Event.class.getName());


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

        return getCardResoponse.getCards().getCard().stream().map((c) -> card2VerifyAllEntry(c)).collect(Collectors.toList());
    }

    public VerifyAllEntry card2VerifyAllEntry(CardInfoType cardInfoType) {
        VerifyAllEntry verifyAllEntry = new VerifyAllEntry();
        verifyAllEntry.setCardInfoType(cardInfoType);
        // Certificates from Card Type
        // eHBA (C.AUT, C.QES)
        CertRefList availableCertificates = new ReadCardCertificate.CertRefList();
        if(cardInfoType.getCardType().equals(CardTypeType.HBA)) {
            availableCertificates.getCertRef().add(CertRefEnum.C_ENC);
            availableCertificates.getCertRef().add(CertRefEnum.C_AUT);
            availableCertificates.getCertRef().add(CertRefEnum.C_QES);
        } else if(cardInfoType.getCardType().equals(CardTypeType.SMC_B)) {
            availableCertificates.getCertRef().add(CertRefEnum.C_ENC);
            availableCertificates.getCertRef().add(CertRefEnum.C_AUT);
            availableCertificates.getCertRef().add(CertRefEnum.C_SIG);
        } else if(cardInfoType.getCardType().equals(CardTypeType.SMC_KT)) {
            // Nothing available
            return verifyAllEntry;
        } else if(cardInfoType.getCardType().equals(CardTypeType.EGK)) {
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

            for(X509DataInfo x509DataInfo : readCardCertificateResponse.getX509DataInfoList().getX509DataInfo()) {
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
    

    @GET
    @Path("/readCardCertificate")
    public ReadCardCertificateResponse readCardCertificate(@QueryParam("CardHandle") String cardHandle) throws Throwable {
        try {
            ReadCardCertificate readCardCertificate = new ReadCardCertificate();

            ReadCardCertificate.CertRefList certRefList = new ReadCardCertificate.CertRefList();
            certRefList.getCertRef().add(CertRefEnum.C_AUT);

            readCardCertificate.setContext(copyValuesFromProxyIntoContextType(contextType));

            Holder<Status> status = new Holder<>();
            Holder<X509DataInfoListType> certList = new Holder<>();

            certificateServicePortType.readCardCertificate(
                    cardHandle,
                    contextType,
                    certRefList,
                    status,
                    certList
            );
            ReadCardCertificateResponse readCardCertificateResponse = new ReadCardCertificateResponse();

            readCardCertificateResponse.setStatus(status.value);
            readCardCertificateResponse.setX509DataInfoList(certList.value);


            String ret = "Certificate read correctly: "+
                    readCardCertificateResponse.getX509DataInfoList();
            //return ret;

            InputStream bayIS = null;

            try {
                bayIS = new ByteArrayInputStream(
                        readCardCertificateResponse
                                .getX509DataInfoList().getX509DataInfo().get(0)
                                .getX509Data().getX509Certificate()
                );
            } catch (Exception e) {
                System.out.println("Error. No certificate Input Stream." +
                        "\n\n\nPossible cause: NO VALID CARD INSERTED INTO KOPS \n\n\n"+e);
            }
            try {
                CertificateFactory certFactory = CertificateFactory
                        .getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
                x509Certificate = (X509Certificate) certFactory.generateCertificate(bayIS);
                System.out.println("The cert has been read from the server");
            } catch (Exception e) {
                System.out.println("Error reading the cert from the server");
            }

            String returnMessage = "The certificate could not be extracted. See other errors in the Terminal";
            if (x509Certificate != null) {
                returnMessage = x509Certificate.toString();
                return readCardCertificateResponse;
            } else return null;

        } catch(Throwable t) {
            log.log(Level.WARNING, "Could not read the Certficiate", t);
            System.out.println("Could not read the Certficiate");
            throw t;
        }
    }

    //for calling internally by verifyCertificate
    public X509Certificate returnCertificate(String mnCardHandle) throws Throwable {
        try {
            ReadCardCertificate readCardCertificate = new ReadCardCertificate();

            ReadCardCertificate.CertRefList certRefList = new ReadCardCertificate.CertRefList();
            certRefList.getCertRef().add(CertRefEnum.C_AUT);

            readCardCertificate.setContext(copyValuesFromProxyIntoContextType(contextType));

            Holder<Status> status = new Holder<>();
            Holder<X509DataInfoListType> certList = new Holder<>();

            certificateServicePortType.readCardCertificate(
                    mnCardHandle,
                    copyValuesFromProxyIntoContextType(contextType),
                    certRefList,
                    status,
                    certList
            );
            ReadCardCertificateResponse readCardCertificateResponse = new ReadCardCertificateResponse();

            readCardCertificateResponse.setStatus(status.value);
            readCardCertificateResponse.setX509DataInfoList(certList.value);


            String ret = "Certificate read correctly: "+
                    readCardCertificateResponse.getX509DataInfoList();
            //return ret;

            InputStream bayIS = null;

            try {
                bayIS = new ByteArrayInputStream(
                        readCardCertificateResponse
                                .getX509DataInfoList().getX509DataInfo().get(0)
                                .getX509Data().getX509Certificate()
                );
            } catch (Exception e) {
                System.out.println("Error. No certificate Input Stream." +
                        "\n\n\nPossible cause: NO VALID CARD INSERTED INTO KOPS \n\n\n"+e);
            }
            try {
                CertificateFactory certFactory = CertificateFactory
                        .getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
                x509Certificate = (X509Certificate) certFactory.generateCertificate(bayIS);
                System.out.println("The cert has been read from the server");
            } catch (Exception e) {
                System.out.println("Error reading the cert from the server");
            }

            String returnMessage = "The certificate could not be extracted. See other errors in the Terminal";
            if (x509Certificate != null) {
                return x509Certificate;
            } else return null;

        } catch(Throwable t) {
            log.log(Level.WARNING, "Could not read the Certficiate", t);
            System.out.println("Could not read the Certficiate");
            throw t;
        }
    }


    @GET
    @Path("/verifyCertificate")
    public String doVerifyCertificate() throws Throwable {
        X509Certificate theC = returnCertificate("");

        try {
            System.out.println("TBSCERT" +theC.getTBSCertificate());
            System.out.println("ENCODED" +theC.getEncoded());
            System.out.println("SIGNATURE" +theC.getSignature());
            System.out.println("SIGALGPARAM" +theC.getSigAlgParams());
            log.log(Level.WARNING, "ENCODED" +theC.getEncoded());
        } catch(Exception e) {
            System.out.println("ENCODING ERROR OF THE CERT: "+e);
        }

        String answer = "No certificate processing yet";

        Holder<Status> status = new Holder<>();
        Holder<VerifyCertificateResponse.VerificationStatus> verificationStatus = new Holder<>();
        Holder<VerifyCertificateResponse.RoleList> arg5 = new Holder<>();
        XMLGregorianCalendar now = null;

        try {
            now = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(TimeZone.getTimeZone("UTC")));
            System.out.println("Creating a timestamp for 'now'");
            answer = "1 timestamp";
        } catch (DatatypeConfigurationException e) {
            System.out.println("Failed to create timestamp for vertification: "+ e);
            answer = "Initial timestamp creation system error";
        }
        try {
            certificateServicePortType.verifyCertificate(contextType, theC.getEncoded(), now, status, verificationStatus, arg5);
            System.out.println("Core verification process in progress...");
            answer = "2 core";
        } catch (Exception e) {
            System.out.println("Core verification process has failed with error: "+e);
            answer = "Core Verification Proecess system error";
        }
        if(verificationStatus.value.getVerificationResult() != VerificationResultType.VALID) {
            System.out.println("Error: Verification process finished. Result Status: "
                    +verificationStatus.value.getVerificationResult());
            answer = "Certifiate was checked. Result: "+verificationStatus.value.getVerificationResult();
        } else answer = "Certifiate was checked. Result: "+verificationStatus.value.getVerificationResult();
        return answer;

        //return "hoo";
    }

    public X509Certificate getX509Certificate() {
        return x509Certificate;
    }

    public void setContextType(ContextType contextType) {
        this.contextType = contextType;
    }

    public void setEventServicePortType(EventServicePortType eventServicePortType) {
        this.eventServicePortType = eventServicePortType;
    }


}
