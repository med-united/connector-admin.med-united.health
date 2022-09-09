package health.medunited.architecture;

import health.medunited.architecture.entities.Todo;
import io.quarkus.runtime.Startup;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
@Startup
public class Bootstrap {
	
	@PersistenceContext
	EntityManager em;

	@PostConstruct
	public void generateDemoData() {
		for(int i=0;i<1000;i++) {
			Todo todo = new Todo();
			todo.setNote(todo.getId());
			em.persist(todo);
		}
	}
}
