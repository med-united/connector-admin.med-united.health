package health.medunited.architecture.entities;

import health.medunited.architecture.odata.annotations.ODataCacheControl;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import java.sql.Timestamp;
import java.util.UUID;


@Entity
@ODataCacheControl(maxAge = 5) // this odata entity set should be cached for 5 seconds
public class Todo {
	// generating uuids might be expensive
	// use with hibernate:
	// @GeneratedValue(generator = "uuid2")
    // @GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Id
	private String id = UUID.randomUUID().toString();
	
	@Version
	private Timestamp lastModified;
	
	private String note;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Timestamp getLastModified() {
		return lastModified;
	}
	public void setLastModified(Timestamp lastModified) {
		this.lastModified = lastModified;
	}
}
