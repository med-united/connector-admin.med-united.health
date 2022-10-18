package health.medunited.architecture.z;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DefaultConnectorServicesProvider extends AbstractConnectorServicesProvider {

    @Inject
    UserConfig userConfig;

    @PostConstruct
    void init() {
        initializeServices();
    }

    @Override
    public UserConfig getUserConfig() {
        return userConfig;
    }

}
