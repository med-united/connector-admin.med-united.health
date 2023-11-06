package health.medunited.cat.logsearch.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class LogEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  private String type;
  private String timestamp;
  private String timestampAsDateTime;
  private String message;
  private String severity;
  @Lob
  private String bytes;

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getTimestampAsDateTime() {
    return this.timestampAsDateTime;
  }

  public void setTimestampAsDateTime(String timestampAsDateTime) {
    this.timestampAsDateTime = timestampAsDateTime;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getSeverity() {
    return this.severity;
  }

  public void setSeverity(String severity) {
    this.severity = severity;
  }

  public String getBytes() {
    return this.bytes;
  }

  public void setBytes(String bytes) {
    this.bytes = bytes;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

}
