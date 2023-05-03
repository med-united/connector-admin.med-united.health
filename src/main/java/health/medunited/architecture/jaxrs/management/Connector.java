package health.medunited.architecture.jaxrs.management;

import health.medunited.architecture.model.RestartRequestBody;

public interface Connector {

    void restart(String connectorUrl, String managementPort, RestartRequestBody managementCredentials);
}
