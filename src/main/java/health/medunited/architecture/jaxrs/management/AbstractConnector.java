package health.medunited.architecture.jaxrs.management;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import health.medunited.architecture.service.common.security.SecretsManagerService;

public abstract class AbstractConnector implements Connector {

    @Inject
    SecretsManagerService secretsManagerService;

    static Consumer<ClientBuilder> modifyClientBuilder = null;

    Client buildClient() {
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();

        clientBuilder.connectTimeout(3, TimeUnit.SECONDS);
        clientBuilder.readTimeout(3, TimeUnit.SECONDS);

        SSLContext sslContext = secretsManagerService.getSslContext();

        clientBuilder.sslContext(sslContext);

        clientBuilder.hostnameVerifier(new HostnameVerifier() {

            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
            
        });

        if(modifyClientBuilder != null) {
            modifyClientBuilder.accept(clientBuilder);
        }
        
        Client client = clientBuilder.build();
        return client;
    }

}
