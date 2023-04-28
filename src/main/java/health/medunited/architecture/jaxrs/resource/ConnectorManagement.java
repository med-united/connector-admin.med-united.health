package health.medunited.architecture.jaxrs.resource;

import health.medunited.architecture.jaxrs.management.Connector;
import health.medunited.architecture.jaxrs.management.ConnectorBrands;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
    public void restart(@PathParam("connectorType") String connectorType) {

        Connector connector = connectorMap.get(connectorType);
        if (connector == null) {
            throw new IllegalArgumentException("Unknown connector type: " + connectorType);
        }
        connector.restart();
    }
}
