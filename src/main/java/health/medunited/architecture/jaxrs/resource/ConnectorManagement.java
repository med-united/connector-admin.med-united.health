package health.medunited.architecture.jaxrs.resource;

import health.medunited.architecture.jaxrs.management.Connector;
import health.medunited.architecture.jaxrs.management.ConnectorBrands;
import health.medunited.architecture.model.ManagementCredentials;
import health.medunited.architecture.service.update.DownloadService;
import health.medunited.architecture.service.update.UpdateService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bouncycastle.asn1.ocsp.ResponderID;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    // @Inject
    // private UpdateService updateService;

    // @Inject
    // private DownloadService downloadService;

    private String managementPort;

    private Map<String, Connector> connectorMap;

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

    // Checks for Updates
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
}
