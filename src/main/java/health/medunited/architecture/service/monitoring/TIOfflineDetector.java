package health.medunited.architecture.service.monitoring;

import health.medunited.architecture.entities.RuntimeConfig;
import health.medunited.architecture.entities.monitoring.actions.TIOfflineActions;
import health.medunited.architecture.entities.monitoring.detections.TIOfflineDetections;
import health.medunited.architecture.monitoring.Scheduler;

import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Singleton
public class TIOfflineDetector {

    @PersistenceContext
    private EntityManager entityManager;

    private boolean isTIOnline;
    private RuntimeConfig runtimeConfig;

    private static final Logger log = Logger.getLogger(TIOfflineDetector.class.getName());

    public TIOfflineDetector() {
        // Default constructor
    }

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

    public void testAddEntryToTable() {
        TIOfflineDetections offlineDetection = new TIOfflineDetections();
        offlineDetection.setGermanDateTime(LocalDateTime.now());
        offlineDetection.setConnectorUrl("https://192.168.178.42");
        offlineDetection.setStatus("OFFLINE");
        // Persist the new instance
        entityManager.persist(offlineDetection);
        log.info("Entry added successfully with ID: " + offlineDetection.getId());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void performAction() {
        log.info("--- Perform action() being executed");

        testAddEntryToTable();

        String getLastEntryOfConnectorInDetections = "SELECT d FROM TIOfflineDetections d WHERE d.connectorUrl = :connectorUrl " +
                "ORDER BY d.germanDateTime DESC";
        TypedQuery<TIOfflineDetections> queryDetections = entityManager.createQuery(getLastEntryOfConnectorInDetections, TIOfflineDetections.class);
        queryDetections.setParameter("connectorUrl", runtimeConfig.getUrl());
        queryDetections.setMaxResults(1);

        TIOfflineDetections lastDetection = queryDetections.getSingleResult();
        log.info("--- Last detection: " + lastDetection.getStatus());
        if (isTIOnline && lastDetection.getStatus().equals(State.OFFLINE.getStatus())) {
            log.info("--- Adding new line to TIOfflineDetections table with status AGAIN_ONLINE");
            // Add new line to TIOfflineDetections table with status "AGAIN_ONLINE" and current timestamp
            TIOfflineDetections newDetection = new TIOfflineDetections();
            newDetection.setGermanDateTime(java.time.LocalDateTime.now());
            newDetection.setConnectorUrl(runtimeConfig.getUrl());
            newDetection.setStatus(State.AGAIN_ONLINE.getStatus());
            entityManager.persist(newDetection);

        } else if (!isTIOnline && lastDetection.getStatus().equals(State.AGAIN_ONLINE.getStatus())) {
            log.info("--- Adding new line to TIOfflineDetections table with status OFFLINE");
            // Add new line to TIOfflineActions table + trigger action
            TIOfflineActions newAction = new TIOfflineActions();
            newAction.setGermanDateTime(java.time.LocalDateTime.now());
            newAction.setConnectorUrl(runtimeConfig.getUrl());
            Action firstAction = Action.values()[0];
            newAction.setAction(firstAction.getCurrentAction());

        } else if (!isTIOnline && !lastDetection.getStatus().equals(State.AGAIN_ONLINE.getStatus())) {
            log.info("--- Adding new line to TIOfflineDetections table with status OFFLINE");
            // Check which was the last action triggered + add new line to this table + add new line to TIOfflineActions + trigger next action
            String getLastEntryOfConnectorInActions = "SELECT d FROM TIOfflineActions d WHERE d.connectorUrl = :connectorUrl " +
                    "ORDER BY d.germanDateTime DESC";
            TypedQuery<TIOfflineActions> queryActions = entityManager.createQuery(getLastEntryOfConnectorInActions, TIOfflineActions.class);
            queryActions.setParameter("connectorUrl", runtimeConfig.getUrl());
            queryActions.setMaxResults(1);

            TIOfflineActions lastAction = queryActions.getSingleResult();
            String actionTriggeredBefore = lastAction.getAction();
            Action nextAction = Action.valueOf(actionTriggeredBefore).getNextAction();

            TIOfflineActions newAction = new TIOfflineActions();
            newAction.setGermanDateTime(java.time.LocalDateTime.now());
            newAction.setConnectorUrl(runtimeConfig.getUrl());
            newAction.setAction(nextAction.getCurrentAction());
            entityManager.persist(newAction);
        }
    }

    public enum State {
        OFFLINE("OFFLINE"),
        AGAIN_ONLINE("AGAIN_ONLINE");

        private final String status;

        State(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

    public enum Action {
        CHECK_FIREWALL("CHECK_FIREWALL", null),
        CHECK_IF_TI_IS_REACHABLE_THROUGH_PORTS("CHECK_IF_TI_IS_REACHABLE_THROUGH_PORTS", CHECK_FIREWALL),
        CHECK_TIME_SYNCHRONIZATION("CHECK_TIME_SYNCHRONIZATION", CHECK_IF_TI_IS_REACHABLE_THROUGH_PORTS),
        CHANGE_VPN_ACCESS_SERVICE_SETTINGS("CHANGE_VPN_ACCESS_SERVICE_SETTINGS", CHECK_TIME_SYNCHRONIZATION),
        RESTART("RESTART", CHANGE_VPN_ACCESS_SERVICE_SETTINGS);

        private final String currentAction;
        private final Action nextAction;

        Action(String currentAction, Action nextAction) {
            this.currentAction = currentAction;
            this.nextAction = nextAction;
        }

        public String getCurrentAction() {
            return currentAction;
        }

        public Action getNextAction() {
            return nextAction;
        }
    }

}
