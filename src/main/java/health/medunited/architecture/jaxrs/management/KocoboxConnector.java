package health.medunited.architecture.jaxrs.management;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import health.medunited.architecture.model.RestartRequestBody;

@ApplicationScoped
@Named("kocobox")
public class KocoboxConnector extends AbstractConnector {

    private static final Logger log = Logger.getLogger(KocoboxConnector.class.getName());

    @Override
    public void restart(String connectorUrl, String managementPort, RestartRequestBody managementCredentials) {
        log.log(Level.INFO, "Restarting Kocobox connector");
    }
}
