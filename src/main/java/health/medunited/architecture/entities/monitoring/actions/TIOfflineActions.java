package health.medunited.architecture.entities.monitoring.actions;

import health.medunited.architecture.odata.annotations.ODataCacheControl;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NamedQuery(name="TIOfflineActions.findAll", query="SELECT r FROM TIOfflineActions r")
@ODataCacheControl(maxAge = 5) // this odata entity set should be cached for 5 seconds
public class TIOfflineActions {

    @Id
    private final String id = UUID.randomUUID().toString();

    private LocalDateTime germanDateTime;

    private String connectorIP;

    private String action;

    private String outcome;

    public TIOfflineActions() {
    }

    public TIOfflineActions(LocalDateTime germanDateTime, String connectorIP, String action, String outcome) {
        this.germanDateTime = germanDateTime;
        this.connectorIP = connectorIP;
        this.action = action;
        this.outcome = outcome;
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

    public String getConnectorIP() {
        return this.connectorIP;
    }

    public void setConnectorIP(String connectorIP) {
        this.connectorIP = connectorIP;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getOutcome() {
        return this.outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
}
