package health.medunited.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;

@ApplicationScoped
public class StatusService {

    public void connectorReachable() {

        ClientBuilder clientBuilder = ClientBuilder.newBuilder();

        Invocation.Builder builder = clientBuilder.build()
                .target("https://192.168.178.42")
                .path("/connector.sds")
                .request();

        Invocation invocation = builder
                .buildGet();
    }

}
