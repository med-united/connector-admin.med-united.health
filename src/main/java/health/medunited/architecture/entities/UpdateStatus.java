package health.medunited.architecture.entities;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import java.sql.Date;

@Entity
public class UpdateStatus {

    // public enum Status {
    //     PENDING, IN_PROGRESS, COMPLETED, FAILED
    // }


    @Id
    private String id = UUID.randomUUID().toString();

    private String version;
    private boolean upToDate;
    private Date lastUpdated;
    private boolean completeAutoUpdate; // when in settings all is switched on
    private String connectorUrl;
    private String runtimeConfig;

    private boolean autoUpdateCheck;
    private boolean autoUpdateDownload;
    private boolean supportTrialUpdates;
    private boolean autoActivateInventoryNetworks;
    private boolean autoUpdate;
    private String autoUpdateWave;
    private String autoUpdateWeekDay;
    private boolean autoUpdateConnectorDependsOnFinishedTerminalUpdates;
    private String autoUpdateTimeHour;
    private String autoUpdateTimeMinute;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean getUpToDate() {
        return upToDate;
    }

    public void setUpToDate(boolean upToDate) {
        this.upToDate = upToDate;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isCompleteAutoUpdate() {
        return completeAutoUpdate;
    }

    public void setCompleteAutoUpdate(boolean completeAutoUpdate) {
        this.completeAutoUpdate = completeAutoUpdate;
    }

    public String getConnectorUrl() {
        return connectorUrl;
    }

    public void setConnectorUrl(String connectorUrl) {
        this.connectorUrl = connectorUrl;
    }

    public String getRuntimeConfig() {
        return runtimeConfig;
    }

    public void setRuntimeConfig(String runtimeConfig) {
        this.runtimeConfig = runtimeConfig;
    }

    public boolean isAutoUpdateCheck() {
        return autoUpdateCheck;
    }

    public void setAutoUpdateCheck(boolean autoUpdateCheck) {
        this.autoUpdateCheck = autoUpdateCheck;
    }

    public boolean isAutoUpdateDownload() {
        return autoUpdateDownload;
    }

    public void setAutoUpdateDownload(boolean autoUpdateDownload) {
        this.autoUpdateDownload = autoUpdateDownload;
    }

    public boolean isSupportTrialUpdates() {
        return supportTrialUpdates;
    }

    public void setSupportTrialUpdates(boolean supportTrialUpdates) {
        this.supportTrialUpdates = supportTrialUpdates;
    }

    public boolean isAutoActivateInventoryNetworks() {
        return autoActivateInventoryNetworks;
    }

    public void setAutoActivateInventoryNetworks(boolean autoActivateInventoryNetworks) {
        this.autoActivateInventoryNetworks = autoActivateInventoryNetworks;
    }

    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public String getAutoUpdateWave() {
        return autoUpdateWave;
    }

    public void setAutoUpdateWave(String autoUpdateWave) {
        this.autoUpdateWave = autoUpdateWave;
    }

    public String getAutoUpdateWeekDay() {
        return autoUpdateWeekDay;
    }

    public void setAutoUpdateWeekDay(String autoUpdateWeekDay) {
        this.autoUpdateWeekDay = autoUpdateWeekDay;
    }

    public boolean isAutoUpdateConnectorDependsOnFinishedTerminalUpdates() {
        return autoUpdateConnectorDependsOnFinishedTerminalUpdates;
    }

    public void setAutoUpdateConnectorDependsOnFinishedTerminalUpdates(boolean autoUpdateConnectorDependsOnFinishedTerminalUpdates) {
        this.autoUpdateConnectorDependsOnFinishedTerminalUpdates = autoUpdateConnectorDependsOnFinishedTerminalUpdates;
    }

    public String getAutoUpdateTimeHour() {
        return autoUpdateTimeHour;
    }

    public void setAutoUpdateTimeHour(String autoUpdateTimeHour) {
        this.autoUpdateTimeHour = autoUpdateTimeHour;
    }

    public String getAutoUpdateTimeMinute() {
        return autoUpdateTimeMinute;
    }

    public void setAutoUpdateTimeMinute(String autoUpdateTimeMinute) {
        this.autoUpdateTimeMinute = autoUpdateTimeMinute;
    }
}