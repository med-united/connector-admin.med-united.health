package health.medunited.architecture.entities;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Konnektor {

    @Id
    private String id = UUID.randomUUID().toString();

    private String runtimeConfigId;
    private String connectorUrl;
    private String version;
    private boolean updateNecessary;
    private boolean autoUpdate;
    private boolean online;
    private int runningOut;
    private String error;

    public String getId() {
        return id;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRuntimeConfigId() {
        return runtimeConfigId;
    }

    public void setRuntimeConfigId(String runtimeConfigId) {
        this.runtimeConfigId = runtimeConfigId;
    }

    public String getConnectorUrl() {
        return connectorUrl;
    }

    public void setConnectorUrl(String connectorUrl) {
        this.connectorUrl = connectorUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public boolean isUpdateNecessary() {
        return updateNecessary;
    }

    public void setUpdateNecessary(boolean updateNecessary) {
        this.updateNecessary = updateNecessary;
    }

    public int getRunningOut() {
        return runningOut;
    }

    public void setRunningOut(int runningOut) {
        this.runningOut = runningOut;
    }

}
