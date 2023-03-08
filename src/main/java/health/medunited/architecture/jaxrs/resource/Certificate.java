package health.medunited.architecture.jaxrs.resource;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.xml.ws.Holder;

import de.gematik.ws.conn.certificateservice.v6.ReadCardCertificate;
import de.gematik.ws.conn.certificateservice.wsdl.v6.CertificateServicePortType;
import de.gematik.ws.conn.certificateservicecommon.v2.X509DataInfoListType;
import de.gematik.ws.conn.connectorcommon.v5.Status;
import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;

@Path("certificate")
public class Certificate {

    private static final Logger log = Logger.getLogger(Event.class.getName());

    @Context
    HttpServletRequest httpServletRequest;

    @Inject
    EventServicePortType eventServicePortType;

    @Inject
    CertificateServicePortType certificateServicePortType;

    ReadCardCertificate.CertRefList certRefList;

    @Inject
    ContextType contextType;


    @GET
    @Path("/getCardHandle")
    public void doGetCardHandle() throws Throwable {
        try {
            ReadCardCertificate readCardCertificate = new ReadCardCertificate();
            readCardCertificate.setContext(copyValuesFromProxyIntoContextType(contextType));
            Holder<Status> status = new Holder<>();
            Holder<X509DataInfoListType> certList = new Holder<>();
            String cardHandle = "";

            certificateServicePortType.readCardCertificate(
                    mnCardHandle, contextType, certRefList, status, certList
            );
        } catch(Throwable t) {
            log.log(Level.WARNING, "Could not read the handle", t);
            System.out.println("Could not read the handle");
            throw t;
        }
    }


    @GET
    @Path("/readCertificate")
    public void doReadCardCertificate(String mnCardHandle) throws Throwable {
        try {
            ReadCardCertificate readCardCertificate = new ReadCardCertificate();
            readCardCertificate.setContext(copyValuesFromProxyIntoContextType(contextType));
            Holder<Status> status = new Holder<>();
            Holder<X509DataInfoListType> certList = new Holder<>();
            String cardHandle = "";

            certificateServicePortType.readCardCertificate(
                    mnCardHandle, contextType, certRefList, status, certList
            );
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

}
