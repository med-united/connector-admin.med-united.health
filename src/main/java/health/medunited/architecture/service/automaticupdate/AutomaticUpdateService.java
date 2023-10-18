package health.medunited.architecture.service.automaticupdate;

import java.util.logging.Level;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import health.medunited.architecture.entities.KonnektorUpdate;
import health.medunited.architecture.entities.RuntimeConfig;
import health.medunited.architecture.jaxrs.management.Connector;
import health.medunited.architecture.jaxrs.management.SecunetConnector;
import health.medunited.architecture.model.ManagementCredentials;
import jakarta.enterprise.event.Observes;
import java.util.logging.Logger;

@Stateless
public class AutomaticUpdateService {

        private static final Logger log = Logger.getLogger(SecunetConnector.class.getName());

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    @Named("secunet")
    private Connector secunetConnector;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void onCheckNeedsUpdate(@Observes RuntimeConfig runtimeConfig) {
        checkNeedsUpdate(runtimeConfig);
    }

    // public void checkNeedsUpdate(RuntimeConfig runtimeConfig) {
    // if(runtimeConfig.getConnectorBrand() != null &&
    // runtimeConfig.getConnectorBrand().equals("SECUNET")) {
    // // Call REST API from Secunet Connector and check if it needs an update
    // // Save the result in the database
    // }
    // entityManager.persist(new KonnektorUpdate());
    // }

    public boolean checkNeedsUpdate(RuntimeConfig runtimeConfig) {
        if (runtimeConfig.getConnectorBrand() != null && runtimeConfig.getConnectorBrand().equals("SECUNET")) {
            // Call REST API from Secunet Connector and check if it needs an update
            // Save the result in the database

            // need to create a boolean from checkUpdate() method
            // secunetConnector.checkUpdate()
        }
        entityManager.persist(new KonnektorUpdate());
        return false;

    }

    public boolean checkDownloadComplete(RuntimeConfig runtimeConfig) {
        if (runtimeConfig.getConnectorBrand() != null && runtimeConfig.getConnectorBrand().equals("SECUNET")) {
            // Call REST API from Secunet Connector and check if Download is complete
            // Save the result in the database
        }
        entityManager.persist(new KonnektorUpdate());
        return false;

    }


    // use in scheduler
    public void automaticUpdates(
            RuntimeConfig runtimeConfig,
            String connectorUrl,
            String updateId,
            String managementPort,
            ManagementCredentials managementCredentials,
            String date) {
        if (checkNeedsUpdate(runtimeConfig)) {

            secunetConnector.downloadUpdate(connectorUrl, managementPort, managementCredentials, updateId);

            // need to check if download is finished,
            if (checkDownloadComplete(runtimeConfig)) {
                secunetConnector.installUpdate(connectorUrl, managementPort, managementCredentials, updateId, date);
            } else {
                log.log(Level.INFO, "Download of Update not completed yet");
            }
        }
    }

}
