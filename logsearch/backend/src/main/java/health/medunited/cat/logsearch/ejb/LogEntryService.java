package health.medunited.cat.logsearch.ejb;

import health.medunited.cat.lib.jaxrs.management.SecunetConnector;
import health.medunited.cat.lib.model.ManagementCredentials;
import health.medunited.cat.logsearch.jpa.LogEntry;
import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
public class LogEntryService {

  private static Logger log = Logger.getLogger(LogEntryService.class.getName());

  Jsonb jsonb = JsonbBuilder.create();

  @PersistenceContext
  EntityManager em;

  // run this function all 60 minutes
  @Schedule(hour = "*", minute = "*", second = "*/10", persistent = false)
  public void fetchAndSaveLogEntries() {
    log.info("Persisting entries");
    LogEntry[] entries = fetchLogEntries();
    for (LogEntry entry : entries) {
      em.persist(entry);
    }
  }

  private LogEntry[] fetchLogEntries() {
    return new LogEntry[]{};
  }
}
