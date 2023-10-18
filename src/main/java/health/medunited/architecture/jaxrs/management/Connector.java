package health.medunited.architecture.jaxrs.management;

import javax.ws.rs.core.Response;

import health.medunited.architecture.model.ManagementCredentials;

public interface Connector {

    void restart(String connectorUrl, String managementPort, ManagementCredentials managementCredentials);

    void restart(String connectorUrl, ManagementCredentials managementCredentials);

    void downloadUpdate(String connectorUrl, String managementPort, ManagementCredentials managementCredentials, String UpdateID);

    void downloadUpdate(String connectorUrl, ManagementCredentials managementCredentials, String UpdateID);

    void installUpdate(String connectorUrl, String managementPort, ManagementCredentials managementCredentials, String updateID, String date);

    void installUpdate(String connectorUrl, ManagementCredentials managementCredentials, String updateID, String date);

    void checkUpdate(String connectorUrl, ManagementCredentials managementCredentials);
    
    void checkUpdate(String connectorUrl, String managementPort, ManagementCredentials managementCredentials);

    boolean isTIOnline(String connectorUrl, String managementPort, ManagementCredentials managementCredentials);
}
