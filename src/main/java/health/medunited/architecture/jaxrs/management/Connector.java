package health.medunited.architecture.jaxrs.management;

import health.medunited.architecture.model.ManagementCredentials;

public interface Connector {

    void restart(String connectorUrl, String managementPort, ManagementCredentials managementCredentials);

    void restart(String connectorUrl, ManagementCredentials managementCredentials);

    boolean isTIOnline(String connectorUrl, String managementPort, ManagementCredentials managementCredentials);
}
