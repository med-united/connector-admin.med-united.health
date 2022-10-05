package health.medunited.architecture.scope;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;

public class ConnectorScopeExtension implements Extension {

    public void addScope(@Observes final BeforeBeanDiscovery event) {
        event.addScope(ConnectorScoped.class, true, false);
    }

    public void registerContext(@Observes final AfterBeanDiscovery event) {
        event.addContext(new ConnectorScopeContext());
    }
}

