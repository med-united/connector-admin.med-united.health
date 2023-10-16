package health.medunited.architecture.jaxrs.resource;

import health.medunited.architecture.jaxrs.management.Connector;
import health.medunited.architecture.jaxrs.management.ConnectorBrands;
import health.medunited.architecture.model.ManagementCredentials;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.time.LocalDate;
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
            @QueryParam("updateId") String updateId) {

        Connector connector = connectorMap.get(connectorType);
        if (connector == null) {
            throw new IllegalArgumentException("Unknown connector type: " + connectorType);
        }

        connector.downloadUpdate(connectorUrl, managementPort, managementCredentials, updateId);
    }

    // Installs downloaded Update on Konnektor
    @POST
    @Path("/{connectorType}/installUpdate")
    @Consumes(MediaType.APPLICATION_JSON)
    public void installUpdate(
            @PathParam("connectorType") String connectorType,
            @QueryParam("connectorUrl") String connectorUrl,
            ManagementCredentials managementCredentials,
            @QueryParam("updateId") String updateId, 
            @QueryParam("date") String date      
            ) {

        Connector connector = connectorMap.get(connectorType);
        if (connector == null) {
            throw new IllegalArgumentException("Unknown connector type: " + connectorType);
        }

        connector.installUpdate(connectorUrl, managementPort, managementCredentials, updateId, date);
    }
}
