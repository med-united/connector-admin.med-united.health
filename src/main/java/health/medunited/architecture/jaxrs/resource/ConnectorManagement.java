package health.medunited.architecture.jaxrs.resource;

import health.medunited.architecture.jaxrs.management.Connector;
import health.medunited.architecture.jaxrs.management.ConnectorBrands;
import health.medunited.architecture.model.RestartRequestBody;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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

    private RestartRequestBody managementCredentials;

    private Map<String, Connector> connectorMap;

    @PostConstruct
    public void init() {
        this.connectorMap = new HashMap<>();
        connectorMap.put(ConnectorBrands.SECUNET.getValue(), secunetConnector);
        connectorMap.put(ConnectorBrands.KOCOBOX.getValue(), kocoboxConnector);
        connectorMap.put(ConnectorBrands.RISE.getValue(), riseConnector);
        managementCredentials = new RestartRequestBody("superadmin", "Pwdpwd12");
    }

    @POST
    @Path("/{connectorType}/restart")
    @Consumes(MediaType.APPLICATION_JSON)
    public void restart(@PathParam("connectorType") String connectorType,
                        @QueryParam("connectorUrl") String connectorUrl,
                        String managementPort,
                        RestartRequestBody managementCredentials
    ) {


        Connector connector = connectorMap.get(connectorType);
        if (connector == null) {
            throw new IllegalArgumentException("Unknown connector type: " + connectorType);
        }
        if (connector == riseConnector) this.managementPort = "8443";
        else this.managementPort = "8500";
        connector.restart(connectorUrl, this.managementPort, this.managementCredentials);
    }
}
