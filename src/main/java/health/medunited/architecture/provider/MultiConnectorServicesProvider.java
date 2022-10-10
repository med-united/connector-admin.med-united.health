package health.medunited.architecture.provider;

import de.gematik.ws.conn.eventservice.wsdl.v7.EventServicePortType;
import health.medunited.architecture.context.ConnectorScopeContext;

import javax.enterprise.context.ApplicationScoped;
import java.util.logging.Logger;

@ApplicationScoped
public class MultiConnectorServicesProvider {
    private final static Logger log = Logger.getLogger(MultiConnectorServicesProvider.class.getName());

    public AbstractConnectorServicesProvider getSingleConnectorServicesProvider(ConnectorScopeContext connectorScopeContext) {
        return new SingleConnectorServicesProvider(connectorScopeContext);
    }

    public EventServicePortType getEventServicePortType(ConnectorScopeContext userConfig) {
        EventServicePortType eventServicePortType = getSingleConnectorServicesProvider(userConfig).getEventServicePortType();
        return eventServicePortType;
    }

}
