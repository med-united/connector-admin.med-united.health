package health.medunited.architecture.jaxrs.resource;

public class ConnectorProductInformation {
    public String connectorVendor;
    public String connectorModel;
    public String connectorDescription;
    public String connectorVersion;

    public ConnectorProductInformation() {

    }

    public void setConnectorVendor(String connectorVendor) {
        this.connectorVendor = connectorVendor;
    }

    public void setConnectorModel(String connectorModel) {
        this.connectorModel = connectorModel;
    }

    public void setConnectorDescription(String connectorDescription) {
        this.connectorDescription = connectorDescription;
    }

    public void setConnectorVersion(String connectorVersion) {
        this.connectorVersion = connectorVersion;
    }
}
