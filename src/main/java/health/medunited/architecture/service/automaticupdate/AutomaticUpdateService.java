package health.medunited.architecture.service.automaticupdate;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import health.medunited.architecture.entities.KonnektorUpdate;
import health.medunited.architecture.entities.RuntimeConfig;
import jakarta.enterprise.event.Observes;

@Stateless
public class AutomaticUpdateService {
    
    @PersistenceContext
    EntityManager entityManager;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void onCheckNeedsUpdate(@Observes RuntimeConfig runtimeConfig) {
        checkNeedsUpdate(runtimeConfig);
    }

    public void checkNeedsUpdate(RuntimeConfig runtimeConfig) {
        if(runtimeConfig.getConnectorBrand() != null && runtimeConfig.getConnectorBrand().equals("SECUNET")) {
            // Call REST API from Secunet Connector and check if it needs an update
            // Save the result in the database
        }
        entityManager.persist(new KonnektorUpdate());
    }
}
