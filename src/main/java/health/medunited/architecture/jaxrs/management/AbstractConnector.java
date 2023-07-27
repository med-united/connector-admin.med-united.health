package health.medunited.architecture.jaxrs.management;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import health.medunited.architecture.service.common.security.SecretsManagerService;

public abstract class AbstractConnector implements Connector {

    public static Logger log = Logger.getLogger(AbstractConnector.class.getName());

    @Inject
    SecretsManagerService secretsManagerService;

    static Consumer<ClientBuilder> modifyClientBuilder = null;

    Client buildClient() {
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();

        clientBuilder.connectTimeout(3, TimeUnit.SECONDS);
        clientBuilder.readTimeout(3, TimeUnit.SECONDS);
        SSLContext sslContext;
        if(secretsManagerService != null)  {
            sslContext = secretsManagerService.getSslContext();
            clientBuilder.sslContext(sslContext);
        }

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

    public void setSecretsManagerService(SecretsManagerService secretsManagerService) {
        this.secretsManagerService = secretsManagerService;
    }

    public SecretsManagerService getSecretsManagerService() {
        return secretsManagerService;
    }
}
