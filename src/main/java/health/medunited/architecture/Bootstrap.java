package health.medunited.architecture;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import health.medunited.architecture.entities.RuntimeConfig;

@Singleton
@Startup
public class Bootstrap {

    @PersistenceContext
    EntityManager em;

    @PostConstruct
    public void generateDemoData() {
        RuntimeConfig runtimeConfig = getRuntimeConfig();
        em.persist(runtimeConfig);
        RuntimeConfig runtimeConfigKops = getRuntimeConfigKops();
        em.persist(runtimeConfigKops);
    }

    public static RuntimeConfig getRuntimeConfig() {
        RuntimeConfig runtimeConfig = new RuntimeConfig();
        runtimeConfig.setId("a2f8d25b-a239-4b9f-945c-1e693c81e4ec");
        runtimeConfig.setUserId("42401d57-15fc-458f-9079-79f6052abad9");
        runtimeConfig.setUrl("https://192.168.178.42");
        runtimeConfig.setSignPort("443");
        runtimeConfig.setVzdPort("636");
        runtimeConfig.setMandantId("Incentergy");
        runtimeConfig.setClientSystemId("Incentergy");
        runtimeConfig.setWorkplaceId("1786_A1");
        runtimeConfig.setClientCertificate("data:application/x-pkcs12;base64,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");
        runtimeConfig.setClientCertificatePassword("N4rouwibGRhne2Fa");
        return runtimeConfig;
    }

    public static RuntimeConfig getRuntimeConfigKops() {
        RuntimeConfig runtimeConfig = new RuntimeConfig();
        runtimeConfig.setId("94310cf3-1bc0-403a-8c2e-44ae2472f0d2");
        runtimeConfig.setUserId("kops-id");
        runtimeConfig.setUrl("http://172.31.26.17");
        runtimeConfig.setMandantId("Mandant1");
        runtimeConfig.setClientSystemId("ClientID1");
        runtimeConfig.setWorkplaceId("Workplace1");
        runtimeConfig.setClientCertificate("data:application/x-pkcs12;base64,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");
        runtimeConfig.setClientCertificatePassword("123456");
        return runtimeConfig;
    }
}