package health.medunited.architecture.entities;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class KonnektorUpdate {
    
    @Id
    private String id = UUID.randomUUID().toString();
    
    private String runtimeConfigId;
    private String connectorUrl;
    private String fromVersion;
    private String toVersion;
    private String updateStatus;

    public String getId() {
        return id;
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
    public String getFromVersion() {
        return fromVersion;
    }
    public void setFromVersion(String fromVersion) {
        this.fromVersion = fromVersion;
    }
    public String getToVersion() {
        return toVersion;
    }
    public void setToVersion(String toVersion) {
        this.toVersion = toVersion;
    }
    public String getUpdateStatus() {
        return updateStatus;
    }
    public void setUpdateStatus(String updateStatus) {
        this.updateStatus = updateStatus;
    }
}
