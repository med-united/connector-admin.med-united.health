package health.medunited.architecture.jaxrs.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.xml.ws.Holder;

import de.gematik.ws.conn.certificateservice.v6.ReadCardCertificate;
import de.gematik.ws.conn.certificateservice.v6.ReadCardCertificateResponse;
import de.gematik.ws.conn.certificateservice.wsdl.v6.CertificateServicePortType;
import de.gematik.ws.conn.certificateservicecommon.v2.CertRefEnum;
import de.gematik.ws.conn.certificateservicecommon.v2.X509DataInfoListType;
import de.gematik.ws.conn.connectorcommon.v5.Status;
import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

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

    ReadCardCertificate.CertRefList certRefList;

    @Inject
    ContextType contextType, ct2;

    String tempCardHandle = "HBA-67";

    @GET
    @Path("/getCardHandle")
    public String doGetCardHandle() throws Throwable {
        try {
            GetCards getCards = new GetCards();
            getCards.setContext(copyValuesFromProxyIntoContextType(contextType));
            return eventServicePortType.getCards(getCards)
                    .getCards().getCard().get(0)
                    .getCardHandle()+" "+
                    eventServicePortType.getCards(getCards)
                            .getCards().getCard().get(1)
                            .getCardHandle()+" "+
                    eventServicePortType.getCards(getCards)
                            .getCards().getCard().get(2)
                            .getCardHandle()+" "+
                    eventServicePortType.getCards(getCards)
                            .getCards().getCard().get(3)
                            .getCardHandle();
        } catch(Throwable t) {
            log.log(Level.WARNING, "Could not get the card handle", t);
            throw t;
        }
    }


    @GET
    @Path("/readCertificate")
    public String doReadCardCertificate(String mnCardHandle) throws Throwable {
        try {
            ReadCardCertificate readCardCertificate = new ReadCardCertificate();

            certRefList = new ReadCardCertificate.CertRefList();
            certRefList.getCertRef().add(CertRefEnum.C_AUT);

            readCardCertificate.setContext(copyValuesFromProxyIntoContextType(contextType));
            contextType = copyValuesFromProxyIntoContextType(ct2);

            Holder<Status> status = new Holder<>();
            Holder<X509DataInfoListType> certList = new Holder<>();

            mnCardHandle = tempCardHandle;

            certificateServicePortType.readCardCertificate(
                    mnCardHandle,
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
                return "The certificate is: \n\n:"+returnMessage;
            } else return returnMessage;



        } catch(Throwable t) {
            log.log(Level.WARNING, "Could not read the Certficiate", t);
            System.out.println("Could not read the Certficiate");
            throw t;
        }
    }

    private static ContextType copyValuesFromProxyIntoContextType(ContextType contextType) {
        ContextType context = new ContextType();
        context.setMandantId(contextType.getMandantId());
        context.setWorkplaceId(contextType.getWorkplaceId());
        context.setClientSystemId(contextType.getClientSystemId());
        context.setUserId(contextType.getUserId());
        return context;
    }

    public X509Certificate getX509Certificate() {
        return x509Certificate;
    }
}
