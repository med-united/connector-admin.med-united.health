package health.medunited.architecture.jaxrs.management;

public interface Connector {

    void restart(String connectorUrl, String managementPort);
}
