package health.medunited.cat.logsearch.ejb;

import health.medunited.cat.logsearch.jpa.LogEntry;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    for (LogEntry entry : Arrays.stream(entries, 1, 4).toArray(LogEntry[]::new)) {
      em.persist(entry);
    }
  }
}
