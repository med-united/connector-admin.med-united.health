package health.medunited.architecture.provider;

import health.medunited.architecture.context.ConnectorScopeContext;
import health.medunited.architecture.service.CardService;
import health.medunited.architecture.service.StatusService;
import health.medunited.architecture.z.DefaultConnectorServicesProvider;
import health.medunited.architecture.z.SecretsManagerService;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

@RequestScoped
public class ConnectorScope implements Callable<Void> {

    private static final Logger logger = java.util.logging.Logger.getLogger(ConnectorScope.class.getName());

    private ConnectorScopeContext connectorScopeContext;

    public void setConnectorScopeContext(ConnectorScopeContext connectorScopeContext) {
        this.connectorScopeContext = connectorScopeContext;
    }

    @Override
    public Void call() throws Exception {
        return null;
    }

    @Produces
    @RequestScoped
    public StatusService createStatusService(SecretsManagerService secretsManagerService,
                                             DefaultConnectorServicesProvider defaultConnectorServicesProvider) {
        return new StatusService(
                connectorScopeContext.getContextType(),
                connectorScopeContext.getUrl(),
                connectorScopeContext.getSslCertificate(),
                connectorScopeContext.getSslCertificatePassword(),
                secretsManagerService,
                defaultConnectorServicesProvider);
    }
}



