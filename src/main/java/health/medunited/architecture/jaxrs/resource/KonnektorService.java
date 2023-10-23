package health.medunited.architecture.jaxrs.resource;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import health.medunited.architecture.entities.Konnektor;

public class KonnektorService {

    @PersistenceContext(unitName = "dashboard")
    private EntityManager em;

    // create / update Konnektor object: url must be unique
    @Transactional
    public void create(
            String runtimeConfigId,
            String connectorUrl,
            String version,
            boolean updateNecessary,
            boolean autoUpdate,
            boolean online,
            int runningOut,
            String error) {

        Konnektor konnektor = new Konnektor();
        konnektor.setRuntimeConfigId(runtimeConfigId);
        konnektor.setConnectorUrl(connectorUrl);
        konnektor.setVersion(version);
        konnektor.setUpdateNecessary(updateNecessary);
        konnektor.setAutoUpdate(autoUpdate);
        konnektor.setOnline(online);
        konnektor.setRunningOut(runningOut);
        konnektor.setError(error);
        em.persist(konnektor);
    }

    @Transactional
    public void create(Konnektor konnektor) {
        em.persist(konnektor);
    }

    // @Transactional
    // public boolean createUniqueUrl(
    // String runtimeConfigId,
    // String connectorUrl,
    // String version,
    // boolean autoUpdate,
    // boolean updateNecessary,
    // int runningOut) {

    // create Konnektor. connectorUrl must be unique
    @Transactional
    public boolean createUniqueUrl(Konnektor konnektor) {
        String connectorUrl = konnektor.getConnectorUrl();

        // Check if a Konnektor with the specified connectorUrl already exists
        TypedQuery<Konnektor> query = em.createQuery(
                "SELECT k FROM Konnektor k WHERE k.connectorUrl = :connectorUrl",
                Konnektor.class);
        query.setParameter("connectorUrl", connectorUrl);
        try {
            Konnektor existingKonnektor = query.getSingleResult();
            // If we get here, a Konnektor with the specified connectorUrl already exists
            return false;
        } catch (NoResultException e) {
            // No Konnektor with the specified connectorUrl exists, so we can save the new
            // one
            em.persist(konnektor);
            return true;
        }
    }

    @Transactional
    public Map<String, Long> getConnectorInfo() {
        Map<String, Long> info = new HashMap<>();

        // Count Konnektors with autoUpdate = true and autoUpdate = false
        long autoUpdateTrueCount = em.createQuery(
                "SELECT COUNT(k) FROM Konnektor k WHERE k.autoUpdate = TRUE", Long.class)
                .getSingleResult();
        long autoUpdateFalseCount = em.createQuery(
                "SELECT COUNT(k) FROM Konnektor k WHERE k.autoUpdate = FALSE", Long.class)
                .getSingleResult();
        info.put("autoUpdate_true", autoUpdateTrueCount);
        info.put("autoUpdate_false", autoUpdateFalseCount);

        // Count all connectorUrl entries
        long connectorUrlCount = em.createQuery(
                "SELECT COUNT(DISTINCT k.connectorUrl) FROM Konnektor k", Long.class)
                .getSingleResult();
        info.put("connectorUrl_count", connectorUrlCount);

        // Count all id entries
        long idCount = em.createQuery(
                "SELECT COUNT(k.id) FROM Konnektor k", Long.class)
                .getSingleResult();
        info.put("id_count", idCount);

        // Count Konnektors with runningOut > 3 and runningOut <= 3
        long runningOutGt3Count = em.createQuery(
                "SELECT COUNT(k) FROM Konnektor k WHERE k.runningOut > 3", Long.class)
                .getSingleResult();
        long runningOutSm3Count = em.createQuery(
                "SELECT COUNT(k) FROM Konnektor k WHERE k.runningOut <= 3", Long.class)
                .getSingleResult();
        info.put("runningOut_gt_3", runningOutGt3Count);
        info.put("runningOut_sm_3", runningOutSm3Count);

        // Count all runtimeConfigId entries
        long runtimeConfigIdCount = em.createQuery(
                "SELECT COUNT(DISTINCT k.runtimeConfigId) FROM Konnektor k", Long.class)
                .getSingleResult();
        info.put("runtimeConfigId_count", runtimeConfigIdCount);

        // Count Konnektors with updateNecessary = true and updateNecessary = false
        long updateNecessaryTrueCount = em.createQuery(
                "SELECT COUNT(k) FROM Konnektor k WHERE k.updateNecessary = TRUE", Long.class)
                .getSingleResult();
        long updateNecessaryFalseCount = em.createQuery(
                "SELECT COUNT(k) FROM Konnektor k WHERE k.updateNecessary = FALSE", Long.class)
                .getSingleResult();
        info.put("updateNecessary_true", updateNecessaryTrueCount);
        info.put("updateNecessary_false", updateNecessaryFalseCount);

        // Count Konnektors with online = true and online = false
        long onlineTrueCount = em.createQuery(
                "SELECT COUNT(k) FROM Konnektor k WHERE k.online = TRUE", Long.class)
                .getSingleResult();
        long onlineFalseCount = em.createQuery(
                "SELECT COUNT(k) FROM Konnektor k WHERE k.online = FALSE", Long.class)
                .getSingleResult();
        info.put("online_true", onlineTrueCount);
        info.put("online_false", onlineFalseCount);

        // Count all version entries
        long versionCount = em.createQuery(
                "SELECT COUNT(DISTINCT k.version) FROM Konnektor k", Long.class)
                .getSingleResult();
        info.put("version_count", versionCount);

        // Count Konnektors with non-null and non-empty error values
        long errorCount = em.createQuery(
                "SELECT COUNT(k) FROM Konnektor k WHERE k.error IS NOT NULL AND k.error <> ''", Long.class)
                .getSingleResult();
        info.put("error_count", errorCount);

        return info;
    }


}
