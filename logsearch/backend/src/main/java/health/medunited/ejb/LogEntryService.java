package health.medunited.ejb;

import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import health.medunited.jpa.LogEntry;

@Stateless
public class LogEntryService {

    private static Logger log = Logger.getLogger(LogEntryService.class.getName());

    Jsonb jsonb = JsonbBuilder.create();

    @PersistenceContext
    EntityManager em;

    // run this function all 60 minutes
    @Schedule  (hour = "*", minute = "*/60", persistent = false)
    public void fetchAndSaveLogEntries() {
        log.info("Persisting entries");
        LogEntry[] entries = fetchLogEntries();
        for (LogEntry entry : entries) {
            em.persist(entry);
        }
    }

    private LogEntry[] fetchLogEntries() {
        return new LogEntry[] { };
    }
}
