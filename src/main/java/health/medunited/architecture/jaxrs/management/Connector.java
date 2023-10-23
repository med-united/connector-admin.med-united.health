package health.medunited.architecture.jaxrs.management;

import javax.ws.rs.core.Response;

import health.medunited.architecture.model.ManagementCredentials;

public interface Connector {

    void restart(String connectorUrl, String managementPort, ManagementCredentials managementCredentials);

    void restart(String connectorUrl, ManagementCredentials managementCredentials);

    void downloadUpdate(String connectorUrl, String managementPort, ManagementCredentials managementCredentials, String UpdateID);

    void downloadUpdate(String connectorUrl, ManagementCredentials managementCredentials, String UpdateID);

    void planUpdate(String connectorUrl, String managementPort, ManagementCredentials managementCredentials, String updateID, String date);

    void planUpdate(String connectorUrl, ManagementCredentials managementCredentials, String updateID, String date);

    void checkUpdate(String connectorUrl, ManagementCredentials managementCredentials);
    
    void checkUpdate(String connectorUrl, String managementPort, ManagementCredentials managementCredentials);

    Response updateSettings(String connectorUrl, ManagementCredentials managementCredentials, boolean autoUpdate);
    
    Response updateSettings(String connectorUrl, String managementPort, ManagementCredentials managementCredentials, boolean autoUpdate);

    Response getReleaseInfo(String connectorUrl, ManagementCredentials managementCredentials);
    
    Response getReleaseInfo(String connectorUrl, String managementPort, ManagementCredentials managementCredentials);

    boolean isTIOnline(String connectorUrl, String managementPort, ManagementCredentials managementCredentials);
}
