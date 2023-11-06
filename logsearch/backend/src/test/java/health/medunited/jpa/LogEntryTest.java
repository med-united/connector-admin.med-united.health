package health.medunited.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import health.medunited.cat.logsearch.jpa.LogEntry;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.junit.jupiter.api.Test;

public class LogEntryTest {

  @Test
  public void readLogEntries() {
    Jsonb jsonb = JsonbBuilder.create();
    LogEntry[] entries = jsonb.fromJson(getClass().getResourceAsStream("/log.json"),
        LogEntry[].class);
    assertEquals(entries.length, 176);
  }
}
