package health.medunited.architecture;

import health.medunited.architecture.entities.RuntimeConfig;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
@Startup
public class Bootstrap {

    @PersistenceContext
    EntityManager em;

    @PostConstruct
    public void generateDemoData() {
        RuntimeConfig runtimeConfig = new RuntimeConfig();
        runtimeConfig.setId("42401d57-15fc-458f-9079-79f6052abad9");
        runtimeConfig.setUrl("192.168.178.42");
        runtimeConfig.setMandantId("Incentergy");
        runtimeConfig.setClientSystemId("Incentergy");
        runtimeConfig.setWorkplaceId("1786_A1");
        runtimeConfig.setUserId("42401d57-15fc-458f-9079-79f6052abad9");
        em.persist(runtimeConfig);

    }
}
