package health.medunited.architecture.provider;

import health.medunited.architecture.context.ConnectorScopeContext;
import health.medunited.architecture.service.CardService;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

@RequestScoped
public class ConnectorScope implements Callable<Void> {

    private static final Logger logger = java.util.logging.Logger.getLogger(ConnectorScope.class.getName());

    private ConnectorScopeContext connectorScopeContext;

    public ConnectorScopeContext getConnectorScopeContext() {
        return connectorScopeContext;
    }

    public void setConnectorScopeContext(ConnectorScopeContext connectorScopeContext) {
        this.connectorScopeContext = connectorScopeContext;
    }

    @Override
    public Void call() throws Exception {
        return null;
    }

    @Produces
    @RequestScoped
    public CardService createCardService() {
        return new CardService(connectorScopeContext.getUrl(), connectorScopeContext.getTerminalId());
    }
}



