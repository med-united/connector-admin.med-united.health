package health.medunited.architecture.jaxrs.management;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import health.medunited.architecture.model.ManagementCredentials;

@ApplicationScoped
@Named("kocobox")
public class KocoboxConnector extends AbstractConnector {

    private static final Logger log = Logger.getLogger(KocoboxConnector.class.getName());

    @Override
    public void restart(String connectorUrl, ManagementCredentials managementCredentials) {
        restart(connectorUrl, "8500", managementCredentials);

    }

    @Override
    public void restart(String connectorUrl, String managementPort, ManagementCredentials managementCredentials) {
        log.log(Level.INFO, "Restarting Kocobox connector");
    }

}
