package health.medunited.architecture.jaxrs.resource;

import static health.medunited.architecture.provider.ContextTypeProducer.copyValuesFromProxyIntoContextType;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

import de.gematik.ws.conn.eventservice.wsdl.v7.FaultMessage;
import health.medunited.architecture.jaxrs.resource.crypto.CryptoLoader;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.crypto.CryptoException;

import de.gematik.ws.conn.cardservice.v8.CardInfoType;
import de.gematik.ws.conn.cardservicecommon.v2.CardTypeType;
import de.gematik.ws.conn.certificateservice.v6.ReadCardCertificate;
import de.gematik.ws.conn.certificateservice.v6.ReadCardCertificate.CertRefList;
import de.gematik.ws.conn.certificateservice.v6.ReadCardCertificateResponse;
import de.gematik.ws.conn.certificateservice.v6.VerifyCertificateResponse;
import de.gematik.ws.conn.certificateservice.wsdl.v6.CertificateServicePortType;
import de.gematik.ws.conn.certificateservicecommon.v2.CertRefEnum;
import de.gematik.ws.conn.certificateservicecommon.v2.X509DataInfoListType;
import de.gematik.ws.conn.certificateservicecommon.v2.X509DataInfoListType.X509DataInfo;
import de.gematik.ws.conn.connectorcommon.v5.Status;
import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;
import health.medunited.architecture.model.VerifyAllEntry;


@Path("certificate")
public class Certificate {

  private static final Logger log = Logger.getLogger(Certificate.class.getName());

  @Context
  HttpServletRequest httpServletRequest;

  @Inject
  EventServicePortType eventServicePortType;

  @Inject
  CertificateServicePortType certificateServicePortType;

  @Inject
  ContextType contextType;

  @GET
  @Path("{certificateType}/{cardHandle}")
  public JsonArray getCertificate(@PathParam("certificateType") String certificateType,
      @PathParam("cardHandle") String cardHandle)
      throws de.gematik.ws.conn.certificateservice.wsdl.v6.FaultMessage,
      CertificateEncodingException, CryptoException, FaultMessage {

    CertRefList certificateTypes = new ReadCardCertificate.CertRefList();
    certificateTypes.getCertRef().add(CertRefEnum.valueOf(certificateType));
    ReadCardCertificateResponse response = getCertificatesFromCard(cardHandle, certificateTypes);
    X509DataInfo xDataInfo = response.getX509DataInfoList().getX509DataInfo().get(0);
    return certSubjectToJSON(xDataInfo);
  }

  @GET
  @Path("/verifyAll")
  public List<VerifyAllEntry> verifyAll() throws FaultMessage {
    GetCards getCards = new GetCards();
    getCards.setContext(copyValuesFromProxyIntoContextType(contextType));

    GetCardsResponse getCardResponse = eventServicePortType.getCards(getCards);

    List<VerifyAllEntry> verificationResult = getCardResponse.getCards().getCard().stream()
        .map(this::card2VerifyAllEntry).collect(Collectors.toList());

    return verificationResult;
  }

  private VerifyAllEntry card2VerifyAllEntry(CardInfoType cardInfoType) {
    VerifyAllEntry verifyAllEntry = new VerifyAllEntry();
    verifyAllEntry.setCardInfoType(cardInfoType);

    CertRefList certificateTypes = getAvailableCertificateTypes(cardInfoType.getCardType());
    if (certificateTypes.getCertRef().isEmpty()) {
      return verifyAllEntry;
    }

    try {
      ReadCardCertificateResponse response =
          getCertificatesFromCard(cardInfoType.getCardHandle(), certificateTypes);
      verifyAllEntry.setReadCardCertificateResponse(response);

      for (X509DataInfo x509DataInfo : response.getX509DataInfoList().getX509DataInfo()) {
        verifyAllEntry.getVerifyCertificateResponse().add(getCertificateVerification(x509DataInfo));
      }
    } catch (de.gematik.ws.conn.certificateservice.wsdl.v6.FaultMessage
        | DatatypeConfigurationException e) {
      log.log(Level.SEVERE, "Could not read certificate", e);
    }
    return verifyAllEntry;
  }

  private VerifyCertificateResponse getCertificateVerification(X509DataInfo x509DataInfo)
      throws DatatypeConfigurationException,
      de.gematik.ws.conn.certificateservice.wsdl.v6.FaultMessage {

    byte[] encodedCertificate = x509DataInfo.getX509Data().getX509Certificate();
    XMLGregorianCalendar now = DatatypeFactory.newInstance()
        .newXMLGregorianCalendar(new GregorianCalendar(TimeZone.getTimeZone("UTC")));
    Holder<Status> status = new Holder<>();
    Holder<VerifyCertificateResponse.VerificationStatus> verificationStatus = new Holder<>();
    Holder<VerifyCertificateResponse.RoleList> roleList = new Holder<>();

    certificateServicePortType.verifyCertificate(copyValuesFromProxyIntoContextType(contextType),
        encodedCertificate, now, status, verificationStatus, roleList);

    VerifyCertificateResponse verifyCertificateResponse = new VerifyCertificateResponse();
    verifyCertificateResponse.setStatus(status.value);
    verifyCertificateResponse.setVerificationStatus(verificationStatus.value);
    verifyCertificateResponse.setRoleList(roleList.value);
    return verifyCertificateResponse;
  }

  private CertRefList getAvailableCertificateTypes(CardTypeType cardType) {
    // eHBA (C.ENC, C.AUT, C.QES)
    CertRefList certificateTypes = new ReadCardCertificate.CertRefList();
    List<CertRefEnum> certRefs = certificateTypes.getCertRef();
    if (cardType.equals(CardTypeType.HBA)) {
      certRefs.add(CertRefEnum.C_ENC);
      certRefs.add(CertRefEnum.C_AUT);
      certRefs.add(CertRefEnum.C_QES);
    } else if (cardType.equals(CardTypeType.SMC_B)) {
      certRefs.add(CertRefEnum.C_ENC);
      certRefs.add(CertRefEnum.C_AUT);
      certRefs.add(CertRefEnum.C_SIG);
    } else if (cardType.equals(CardTypeType.SMC_KT)) {
      // Nothing available
    } else if (cardType.equals(CardTypeType.EGK)) {
      certRefs.add(CertRefEnum.C_AUT);
    }
    return certificateTypes;
  }

  private ReadCardCertificateResponse getCertificatesFromCard(String cardHandle,
      CertRefList certificateTypes)
      throws de.gematik.ws.conn.certificateservice.wsdl.v6.FaultMessage {
    Holder<Status> status = new Holder<>();
    Holder<X509DataInfoListType> certList = new Holder<>();
    certificateServicePortType.readCardCertificate(cardHandle,
        copyValuesFromProxyIntoContextType(contextType), certificateTypes, status, certList);
    ReadCardCertificateResponse readCardCertificateResponse = new ReadCardCertificateResponse();
    readCardCertificateResponse.setStatus(status.value);
    readCardCertificateResponse.setX509DataInfoList(certList.value);
    return readCardCertificateResponse;
  }

  private JsonArray certSubjectToJSON(X509DataInfo xDataInfo)
      throws de.gematik.ws.conn.certificateservice.wsdl.v6.FaultMessage, CryptoException,
      CertificateEncodingException, FaultMessage {
    X509Certificate certificate = CryptoLoader
        .getCertificateFromAsn1DERCertBytes(xDataInfo.getX509Data().getX509Certificate());
    X500Name x500subject = new JcaX509CertificateHolder(certificate).getSubject();

    // C=DE,L=Freiburg,PostalCode=79114,STREET=Sundgauallee 59,
    // SERIALNUMBER=80276883110000118001,CN=VincenzkrankenhausTEST-ONLY

    JsonArrayBuilder items = Json.createArrayBuilder();
    for (RDN rdn : x500subject.getRDNs()) {
      for (AttributeTypeAndValue tv : rdn.getTypesAndValues()) {
        items.add(
            Json.createObjectBuilder().add("field", BCStyle.INSTANCE.oidToDisplayName(tv.getType()))
                .add("value", tv.getValue().toString()));
      }
    }
    return items.build();
  }
}
