package health.medunited.architecture.jaxrs.resource;

public class MonitoringRequest {


    private boolean updateConnectors;
    private boolean updateCardTerminals;
    private boolean checkTIStatusOnline;


    // getters
    public boolean isUpdateConnectors() {
        return updateConnectors;
    }

    public boolean isUpdateCardTerminals() {
        return updateCardTerminals;
    }

    public boolean isCheckTIStatusOnline() {
        return checkTIStatusOnline;
    }


    //setters
    public void setUpdateConnectors(boolean updateConnectors) {
        this.updateConnectors = updateConnectors;
    }

    public void setUpdateCardTerminals(boolean updateCardTerminals) {
        this.updateCardTerminals = updateCardTerminals;
    }

    public void setCheckTIStatusOnline(boolean checkTIStatusOnline) {
        this.checkTIStatusOnline = checkTIStatusOnline;
    }


    //constructor

    public MonitoringRequest() {

    }

    public MonitoringRequest(boolean updateConnectors,
                      boolean updateCardTerminals,
                      boolean checkTIStatusOnline) {

        this.updateConnectors = updateConnectors;
        this.updateCardTerminals = updateCardTerminals;
        this.checkTIStatusOnline = checkTIStatusOnline;

    }

}
