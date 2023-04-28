package health.medunited.architecture.jaxrs.management;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Named("rise")
public class RiseConnector implements Connector{

    private static final Logger log = Logger.getLogger(RiseConnector.class.getName());

    @Override
    public void restart() {
        log.log(Level.INFO, "Restarting Rise connector");
    }
}
