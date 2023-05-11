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
    @Named("secu_kon") // Secunet connector
    private Connector secunetConnector;

    @Inject
    @Named("kocobox") // KocoBox connector
    private Connector kocoboxConnector;

    @Inject
    @Named("RKONN") // RISE connector
    private Connector riseConnector;

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
    public void restart(@PathParam("connectorType") String connectorType,
                        @QueryParam("connectorUrl") String connectorUrl,
                        @QueryParam("managementPort") String managementPort,
                        RestartRequestBody managementCredentials
    ) {

        Connector connector = connectorMap.get(connectorType);
        if (connector == null) {
            throw new IllegalArgumentException("Unknown connector type: " + connectorType);
        }
        connector.restart(connectorUrl, managementPort, managementCredentials);
    }
}
