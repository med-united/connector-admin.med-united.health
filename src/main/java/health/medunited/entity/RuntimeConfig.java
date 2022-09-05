package health.medunited.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;

@Entity(name="runtimeconfig")
public class RuntimeConfig extends PanacheEntity {

    private String connectorUrl;

    public String getConnectorUrl() {
        return connectorUrl;
    }

    public void setConnectorUrl(String connectorUrl) {
        this.connectorUrl = connectorUrl;
    }
}
