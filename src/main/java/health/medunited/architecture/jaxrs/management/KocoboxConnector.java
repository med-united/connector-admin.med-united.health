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

    @Override
    public boolean isTIOnline(String connectorUrl, String managementPort, ManagementCredentials managementCredentials) {
        return true;
    }

    // Downloads update files on Connector
    // Should be usable also for downgrades
    @Override
    public void downloadUpdate(String connectorUrl, ManagementCredentials managementCredentials, String UpdateID) {
        downloadUpdate(connectorUrl, "8500", managementCredentials, UpdateID);
    }

    @Override
    public void downloadUpdate(String connectorUrl, String managementPort, ManagementCredentials managementCredentials, String UpdateID) {
        log.log(Level.INFO, "Downloading Update On Kocobox connector");

    }


    // Installs downloaded update files on Connector
    @Override
    public void installUpdate(String connectorUrl, ManagementCredentials managementCredentials, String updateID, String date) {
        installUpdate(connectorUrl, "8500", managementCredentials, updateID, date);
    }

    @Override
    public void installUpdate(String connectorUrl, String managementPort, ManagementCredentials managementCredentials, String updateID, String date) {
        log.log(Level.INFO, "Installing Update On Kocobox connector");

    }

    
}
