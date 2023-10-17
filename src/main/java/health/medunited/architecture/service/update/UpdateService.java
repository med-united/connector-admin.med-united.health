package health.medunited.architecture.service.update;

import javax.persistence.EntityManager;

import health.medunited.architecture.entities.KonnektorUpdate;

public class UpdateService {

    private final EntityManager entityManager;

    public UpdateService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void recordUpdate(
            String runtimeConfigId,
            String connectorUrl, 
            String fromVersion, 
            String toVersion) {
        KonnektorUpdate update = new KonnektorUpdate();
        update.setRuntimeConfigId(runtimeConfigId);
        update.setConnectorUrl(connectorUrl);
        update.setFromVersion(fromVersion);
        update.setToVersion(toVersion);
        save(update);
    }

    private void save(KonnektorUpdate update) {
        entityManager.persist(update);
    }

}
