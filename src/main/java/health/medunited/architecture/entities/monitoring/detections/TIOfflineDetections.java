package health.medunited.architecture.entities.monitoring.detections;

import health.medunited.architecture.odata.annotations.ODataCacheControl;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NamedQuery(name="TIOfflineDetections.findAll", query="SELECT r FROM TIOfflineDetections r")
@ODataCacheControl(maxAge = 5) // this odata entity set should be cached for 5 seconds
public class TIOfflineDetections {

    @Id
    private final String id = UUID.randomUUID().toString();

    private LocalDateTime germanDateTime;

    private String connectorUrl;

    private String status;

    public TIOfflineDetections() {
    }

    public TIOfflineDetections(LocalDateTime germanDateTime, String connectorUrl, String status) {
        this.germanDateTime = germanDateTime;
        this.connectorUrl = connectorUrl;
        this.status = status;
    }

    public String getId() {
        return this.id;
    }

    public LocalDateTime getGermanDateTime() {
        return this.germanDateTime;
    }

    public void setGermanDateTime(LocalDateTime germanDateTime) {
        this.germanDateTime = germanDateTime;
    }

    public String getConnectorUrl() {
        return this.connectorUrl;
    }

    public void setConnectorUrl(String connectorIp) {
        this.connectorUrl = connectorIp;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
