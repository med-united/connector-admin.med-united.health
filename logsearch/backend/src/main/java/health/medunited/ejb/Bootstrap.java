package health.medunited.ejb;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import health.medunited.jpa.LogEntry;

@Singleton
@Startup
public class Bootstrap {

  private static Logger log = Logger.getLogger(LogEntryService.class.getName());

  Jsonb jsonb = JsonbBuilder.create();

  @PersistenceContext
  EntityManager em;

  @PostConstruct
  public void install() {
    LogEntry[] entries = jsonb.fromJson(getClass().getResourceAsStream("/log.json"),
        LogEntry[].class);
    for (LogEntry entry : entries) {
      em.persist(entry);
    }
  }
}
