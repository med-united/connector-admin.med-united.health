package health.medunited.architecture.jaxrs.management;

import health.medunited.architecture.model.RestartRequestBody;
import health.medunited.architecture.service.common.security.SecretsManagerService;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Named("rise")
public class RiseConnector implements Connector{


    private static final Logger log = Logger.getLogger(SecunetConnector.class.getName());

    @Inject
    SecretsManagerService secretsManagerService;

    @Override
    public void restart(String connectorUrl, String managementPort, RestartRequestBody managementCredentials) {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
        log.log(Level.INFO, "Restarting RISE connector");
        log.log(Level.INFO, "Restarting RISE connector");
        log.log(Level.INFO, "Restarting RISE connector");
        log.log(Level.INFO, "Restarting RISE connector");

        log.log(Level.WARNING, "Trusting all certificates. Do not use in production mode!");

        /*
        Client client;
        ApacheHttpClient4Engine engine = null;
        try {
            engine = new ApacheHttpClient4Engine(createAllTrustingClient());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        client = new ResteasyClientBuilder().httpEngine(engine).build();

        Response loginResponse = loginManagementConsole(client, connectorUrl, managementPort, managementCredentials);

        System.out.println(loginResponse.getHeaders());

        */


    }

    public DefaultHttpClient createAllTrustingClient() throws GeneralSecurityException {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

        TrustStrategy trustStrategy = new TrustStrategy() {

            @Override
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                log.log(Level.INFO,"Is trusted? return true");
                return true;
            }
        };

        SSLSocketFactory factory = new SSLSocketFactory(trustStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        registry.register(new Scheme("https", 443, factory));

        ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(registry);
        mgr.setMaxTotal(1000);
        mgr.setDefaultMaxPerRoute(1000);

        DefaultHttpClient client = new DefaultHttpClient(mgr, new DefaultHttpClient().getParams());
        return client;
    }

    private Response loginManagementConsole(Client client, String connectorUrl, String managementPort, RestartRequestBody managementCredentials) {
        WebTarget loginTarget = client.target(connectorUrl + ":" + managementPort)
                .path("/api/v1/auth/login");

        Invocation.Builder loginBuilder = loginTarget.request(MediaType.APPLICATION_JSON);
        return loginBuilder.post(Entity.json(managementCredentials));
    }
}
