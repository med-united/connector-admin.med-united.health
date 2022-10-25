package health.medunited.architecture.service;

import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;
import de.gematik.ws.conn.eventservice.wsdl.v7.FaultMessage;
import health.medunited.architecture.provider.AbstractConnectorServicesProvider;
import health.medunited.architecture.provider.DefaultConnectorServicesProvider;

import javax.enterprise.inject.Alternative;

@Alternative
public class StatusService {

    private ContextType contextType;
    private String url;
    private String sslCertificate;
    private String sslCertificatePassword;
    private AbstractConnectorServicesProvider connectorServicesProvider;

    public StatusService(ContextType contextType, String url, String sslCertificate, String sslCertificatePassword,
                         AbstractConnectorServicesProvider connectorServicesProvider) {
        this.contextType = contextType;
        this.url = url;
        this.sslCertificate = sslCertificate;
        this.sslCertificatePassword = sslCertificatePassword;
        this.connectorServicesProvider = connectorServicesProvider;
    }

    public StatusService() {
    }

    public void getStatus() throws FaultMessage {

        connectorServicesProvider.setSslCredentials(sslCertificate, sslCertificatePassword);

        GetCards parameter = new GetCards();
        parameter.setContext(getContextType());

        EventServicePortType service = connectorServicesProvider.getEventServicePortType();

        GetCardsResponse r = service.getCards(parameter);
    }

    public ContextType getContextType() {
        return contextType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
