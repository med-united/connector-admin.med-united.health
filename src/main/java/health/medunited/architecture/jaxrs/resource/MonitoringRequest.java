package health.medunited.architecture.jaxrs.resource;

public class MonitoringRequest {


    private boolean updateConnectorsOn;
    private boolean updateCardTerminalsOn;
    private boolean checkTIStatusOnlineOn;


    // getters
    public boolean isUpdateConnectorsOn() {
        return updateConnectorsOn;
    }

    public boolean isUpdateCardTerminalsOn() {
        return updateCardTerminalsOn;
    }

    public boolean isCheckTIStatusOnlineOn() {
        return checkTIStatusOnlineOn;
    }


    //setters
    public void setUpdateConnectorsOn(boolean updateConnectorsOn) {
        this.updateConnectorsOn = updateConnectorsOn;
    }

    public void setUpdateCardTerminalsOn(boolean updateCardTerminalsOn) {
        this.updateCardTerminalsOn = updateCardTerminalsOn;
    }

    public void setCheckTIStatusOnlineOn(boolean checkTIStatusOnlineOn) {
        this.checkTIStatusOnlineOn = checkTIStatusOnlineOn;
    }


    //constructor

    public MonitoringRequest() {

    }

    public MonitoringRequest(boolean updateConnectorsOn,
                      boolean updateCardTerminalsOn,
                      boolean checkTIStatusOnlineOn) {

        this.updateConnectorsOn = updateConnectorsOn;
        this.updateCardTerminalsOn = updateCardTerminalsOn;
        this.checkTIStatusOnlineOn = checkTIStatusOnlineOn;

    }

}
