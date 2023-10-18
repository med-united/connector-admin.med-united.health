package health.medunited.architecture.jaxrs.resource;

import java.sql.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import health.medunited.architecture.entities.UpdateStatus;


public class UpdateManagementService {

    @PersistenceContext(unitName = "dashboard")
    private EntityManager em;


    // Saves infos about Update Status in DB
    @Transactional
    public void updateStatus(
            String version, 
            boolean upToDate, 
            Date lastUpdated, 
            boolean completeAutoUpdate, 
            String connectorUrl, 
            String runtimeConfig, 
            boolean autoUpdateCheck, 
            boolean autoUpdateDownload,  
            boolean supportTrialUpdates, 
            boolean autoActivateInventoryNetworks, 
            boolean autoUpdate, 
            String autoUpdateWave, 
            String autoUpdateWeekDay, 
            boolean autoUpdateConnectorDependsOnFinishedTerminalUpdates, 
            String autoUpdateTimeHour, 
            String autoUpdateTimeMinute
        ) {
        UpdateStatus updateStatus = new UpdateStatus();
        updateStatus.setVersion(version);
        updateStatus.setUpToDate(upToDate);
        updateStatus.setLastUpdated(lastUpdated);
        updateStatus.setCompleteAutoUpdate(completeAutoUpdate);
        updateStatus.setConnectorUrl(connectorUrl);
        updateStatus.setRuntimeConfig(runtimeConfig);
    
        updateStatus.setAutoUpdateCheck(autoUpdateCheck);
        updateStatus.setAutoUpdateDownload(autoUpdateDownload);
        updateStatus.setSupportTrialUpdates(supportTrialUpdates);
        updateStatus.setAutoActivateInventoryNetworks(autoActivateInventoryNetworks);
        updateStatus.setAutoUpdate(autoUpdate);
        updateStatus.setAutoUpdateWave(autoUpdateWave);
        updateStatus.setAutoUpdateWeekDay(autoUpdateWeekDay);
        updateStatus.setAutoUpdateConnectorDependsOnFinishedTerminalUpdates(autoUpdateConnectorDependsOnFinishedTerminalUpdates);
        updateStatus.setAutoUpdateTimeHour(autoUpdateTimeHour);
        updateStatus.setAutoUpdateTimeMinute(autoUpdateTimeMinute);
        em.persist(updateStatus);
    }
    
}
