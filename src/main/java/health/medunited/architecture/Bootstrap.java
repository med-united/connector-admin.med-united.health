package health.medunited.architecture;

import health.medunited.architecture.entities.ProductCollection;
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
		for(int i=0;i<5;i++) {
			RuntimeConfig runtimeConfig = new RuntimeConfig();
			runtimeConfig.setUrl("testUrl");
			em.persist(runtimeConfig);
		}
		for(int i=0;i<5;i++) {
			ProductCollection productCollection = new ProductCollection();
			em.persist(productCollection);
		}
	}
}
