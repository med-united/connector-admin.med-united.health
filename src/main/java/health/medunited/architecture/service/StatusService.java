package health.medunited.architecture.service;

import de.gematik.ws.conn.connectorcontext.v2.ContextType;
import de.gematik.ws.conn.eventservice.v7.GetCards;
import de.gematik.ws.conn.eventservice.v7.GetCardsResponse;
import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;
import de.gematik.ws.conn.eventservice.wsdl.v7.FaultMessage;
import health.medunited.architecture.model.RuntimeConfig;
import health.medunited.architecture.provider.AbstractConnectorServicesProvider;
import health.medunited.architecture.provider.DefaultConnectorServicesProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

@RequestScoped
public class StatusService {

    private ContextType contextType;
    private String url;
    private String sslCertificate;
    private String sslCertificatePassword;
    @Inject
    AbstractConnectorServicesProvider connectorServicesProvider;

    public void getStatus(RuntimeConfig runtimeConfig) throws FaultMessage {

        ContextType test = new ContextType();
        test.setClientSystemId("Incentergy");
        test.setMandantId("Incentergy");
        test.setWorkplaceId("1786_A1");
        test.setUserId("42401d57-15fc-458f-9079-79f6052abad9");

        connectorServicesProvider.setSslCredentials(runtimeConfig.getClientCertificate(), runtimeConfig.getClientCertificatePassword());

        GetCards parameter = new GetCards();
        parameter.setContext(test);

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
