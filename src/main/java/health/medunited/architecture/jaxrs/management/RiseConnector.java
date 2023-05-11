package health.medunited.architecture.jaxrs.management;

import health.medunited.architecture.model.RestartRequestBody;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Named("RKONN")
public class RiseConnector implements Connector{

    private static final Logger log = Logger.getLogger(RiseConnector.class.getName());

    @Override
    public void restart(String connectorUrl, String managementPort, RestartRequestBody managementCredentials) {
        log.log(Level.INFO, "Restarting Rise connector");
    }
}
