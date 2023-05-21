package health.medunited.architecture.jaxrs.resource;

import health.medunited.architecture.jaxrs.management.Connector;
import health.medunited.architecture.jaxrs.management.ConnectorBrands;
import health.medunited.architecture.model.RestartRequestBody;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.stream.JsonParser;

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
    }

    @POST
    @Path("/{connectorType}/restart")
    @Consumes(MediaType.APPLICATION_JSON)
    public void restart(
            @PathParam("connectorType") String connectorType,
            @QueryParam("connectorUrl") String connectorUrl,
            String credentials
    ) {
        JsonParser parser = Json.createParser(new StringReader(credentials));
        parser.next();
        parser.next();
        parser.next();

        String username = parser.getString();
        parser.next();
        parser.next();
        String password = parser.getString();

        managementCredentials = new RestartRequestBody(username,password);

        Connector connector = connectorMap.get(connectorType);
        if (connector == null) {
            throw new IllegalArgumentException("Unknown connector type: " + connectorType);
        }
        if (connector == riseConnector) managementPort = "8443";
        else managementPort = "8500";
        connector.restart(connectorUrl, managementPort, managementCredentials);

    }
}
