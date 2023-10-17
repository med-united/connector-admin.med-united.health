package health.medunited.architecture.service.update;

import javax.persistence.EntityManager;

import health.medunited.architecture.entities.KonnektorUpdate;

public class DownloadService {

    private final EntityManager entityManager;

    public DownloadService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void recordUpdateDownload(
            String runtimeConfigId,
            String connectorUrl, 
            String version) {
        KonnektorUpdate update = new KonnektorUpdate();
        update.setRuntimeConfigId(runtimeConfigId);
        update.setConnectorUrl(connectorUrl);
        update.setToVersion(version);
        save(update);
    }

    private void save(KonnektorUpdate update) {
        entityManager.persist(update);
    }

}
