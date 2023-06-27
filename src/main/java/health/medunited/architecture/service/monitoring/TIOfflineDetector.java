package health.medunited.architecture.service.monitoring;

import health.medunited.architecture.entities.RuntimeConfig;
import health.medunited.architecture.entities.monitoring.detections.TIOfflineDetections;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

public class TIOfflineDetector {

    @PersistenceContext
    private EntityManager entityManager;

    private boolean isTIOnline;
    private State status;
    private RuntimeConfig runtimeConfig;

    public TIOfflineDetector(boolean isTIOnline, RuntimeConfig runtimeConfig) {
        this.isTIOnline = isTIOnline;
        this.runtimeConfig = runtimeConfig;
    }

    public void setIsTIOnline(boolean isTIOnline) {
        this.isTIOnline = isTIOnline;
    }

    public void setRuntimeConfig(RuntimeConfig runtimeConfig) {
        this.runtimeConfig = runtimeConfig;
    }

    public void performAction() {
        if (isTIOnline) {
            // Check if last entry of the connector in the TIOfflineDetections table is "STILL_OFFLINE"
            String queryStr = "SELECT d FROM TIOfflineDetections d WHERE d.connectorIP = :connectorIP " +
                    "AND d.status = :status ORDER BY d.germanDateTime DESC";
            TypedQuery<TIOfflineDetections> query = entityManager.createQuery(queryStr, TIOfflineDetections.class);
            query.setParameter("connectorIP", runtimeConfig.getUrl());
            query.setParameter("status", State.STILL_OFFLINE.getStatus());
            query.setMaxResults(1);

            TIOfflineDetections lastDetection = query.getSingleResult();
        } else {
            // Check if last entry of the connector in the TIOfflineDetections table is "AGAIN_ONLINE"
        }
    }

    public enum State {
        STILL_OFFLINE("STILL_OFFLINE"),
        AGAIN_ONLINE("AGAIN_ONLINE");

        private final String status;

        State(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

}
