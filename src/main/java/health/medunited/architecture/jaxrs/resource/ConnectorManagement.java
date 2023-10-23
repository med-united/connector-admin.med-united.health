package health.medunited.architecture.jaxrs.resource;

import health.medunited.architecture.jaxrs.management.Connector;
import health.medunited.architecture.jaxrs.management.ConnectorBrands;
import health.medunited.architecture.model.ManagementCredentials;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

@Path("management")
public class ConnectorManagement {

    @Inject
    @Named("secunet")
    private Connector secunetConnector;

    @Inject
    @Named("kocobox")
    private Connector kocoboxConnector;

    @Inject
    @Named("rise")
    private Connector riseConnector;

    private String managementPort;

    private Map<String, Connector> connectorMap;

    @PersistenceContext(unitName = "dashboard")
    private EntityManager em;

    @PostConstruct
    public void init() {
        this.connectorMap = new HashMap<>();
        connectorMap.put(ConnectorBrands.SECUNET.getValue(), secunetConnector);
        connectorMap.put(ConnectorBrands.KOCOBOX.getValue(), kocoboxConnector);
        connectorMap.put(ConnectorBrands.RISE.getValue(), riseConnector);
    }

    @POST
    @Path("/{connectorType}/restart")
    @Consumes(MediaType.APPLICATION_JSON)
    public void restart(
            @PathParam("connectorType") String connectorType,
            @QueryParam("connectorUrl") String connectorUrl,
            ManagementCredentials managementCredentials) {

        Connector connector = connectorMap.get(connectorType);
        if (connector == null) {
            throw new IllegalArgumentException("Unknown connector type: " + connectorType);
        }

        connector.restart(connectorUrl, managementCredentials);

    }

    @GET
    @Path("/{connectorType}/checkTIStatus")
    @Consumes(MediaType.APPLICATION_JSON)
    public void checkTIStatus(
            @PathParam("connectorType") String connectorType,
            @QueryParam("connectorUrl") String connectorUrl,
            ManagementCredentials managementCredentials) {

        Connector connector = connectorMap.get(connectorType);
        if (connector == null) {
            throw new IllegalArgumentException("Unknown connector type: " + connectorType);
        }

        connector.isTIOnline(connectorUrl, managementPort, managementCredentials);
    }

    // Downloads Update to Konnektor
    @POST
    @Path("/{connectorType}/downloadUpdate")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadUpdate(
            @PathParam("connectorType") String connectorType,
            @QueryParam("connectorUrl") String connectorUrl,
            ManagementCredentials managementCredentials,
            @QueryParam("runtimeConfigId") String runtimeConfigId,
            @QueryParam("updateId") String updateId) {

        Connector connector = connectorMap.get(connectorType);
        if (connector == null) {
            throw new IllegalArgumentException("Unknown connector type: " + connectorType);
        }

        connector.downloadUpdate(connectorUrl, managementPort, managementCredentials, updateId);
        // downloadService.recordUpdateDownload(runtimeConfigId, connectorUrl,
        // updateId);

    }

    // Installs downloaded Update on Konnektor
    @POST
    @Path("/{connectorType}/installUpdate")
    @Consumes(MediaType.APPLICATION_JSON)
    public void planUpdate(
            @PathParam("connectorType") String connectorType,
            @QueryParam("connectorUrl") String connectorUrl,
            ManagementCredentials managementCredentials,
            @QueryParam("updateId") String updateId,
            @QueryParam("runtimeConfigId") String runtimeConfigId,
            @QueryParam("date") String date) {

        Connector connector = connectorMap.get(connectorType);
        if (connector == null) {
            throw new IllegalArgumentException("Unknown connector type: " + connectorType);
        }
        String fromVersion = "1.0test";

        connector.planUpdate(connectorUrl, managementPort, managementCredentials, updateId, date);
        // updateService.recordUpdate(runtimeConfigId, connectorUrl, fromVersion,
        // updateId);
    }

    // Checks for Updates
    @POST
    @Path("/{connectorType}/checkUpdates")
    @Consumes(MediaType.APPLICATION_JSON)
    public void checkUpdates(
            @PathParam("connectorType") String connectorType,
            @QueryParam("connectorUrl") String connectorUrl,
            ManagementCredentials managementCredentials) {

        Connector connector = connectorMap.get(connectorType);
        if (connector == null) {
            throw new IllegalArgumentException("Unknown connector type: " + connectorType);
        }

        connector.checkUpdate(connectorUrl, managementCredentials);
    }

    // Update Settings
    @POST
    @Path("/{connectorType}/updateSettings")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateSettings(
            @PathParam("connectorType") String connectorType,
            @QueryParam("connectorUrl") String connectorUrl,
            @QueryParam("autoUpdate") boolean autoUpdate,
            ManagementCredentials managementCredentials) {

        Connector connector = connectorMap.get(connectorType);
        if (connector == null) {
            throw new IllegalArgumentException("Unknown connector type: " + connectorType);
        }
        return connector.updateSettings(connectorUrl, managementCredentials, autoUpdate);
    }


    // Get Release Infos
    @GET
    @Path("/{connectorType}/getReleaseInfo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReleaseInfo(
            @PathParam("connectorType") String connectorType,
            @QueryParam("connectorUrl") String connectorUrl,
            ManagementCredentials managementCredentials) {

        Connector connector = connectorMap.get(connectorType);
        if (connector == null) {
            throw new IllegalArgumentException("Unknown connector type: " + connectorType);
        }
        return connector.getReleaseInfo(connectorUrl, managementCredentials);
    }


}
