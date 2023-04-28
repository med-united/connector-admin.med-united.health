package health.medunited.architecture.jaxrs.management;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Named("kocobox")
public class KocoboxConnector implements Connector{

    private static final Logger log = Logger.getLogger(KocoboxConnector.class.getName());

    @Override
    public void restart(String connectorUrl, String managementPort) {
        log.log(Level.INFO, "Restarting Kocobox connector");
    }
}
