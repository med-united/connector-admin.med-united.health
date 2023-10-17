package health.medunited.architecture.entities;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class KonnektorDownload {
    
    @Id
    private String id = UUID.randomUUID().toString();
    private String runtimeConfigId;
    private String connectorUrl;
    private String version;
    private String downloadStatus;

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
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getdownloadStatus() {
        return downloadStatus;
    }
    public void setdDownloadStatus(String downloadStatus) {
        this.downloadStatus = downloadStatus;
    }
}
