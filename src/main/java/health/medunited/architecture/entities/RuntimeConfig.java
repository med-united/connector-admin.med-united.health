package health.medunited.architecture.entities;

import health.medunited.architecture.odata.annotations.ODataCacheControl;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@ODataCacheControl(maxAge = 5) // this odata entity set should be cached for 5 seconds
public class RuntimeConfig {

    @Id
    private String id = UUID.randomUUID().toString();

    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
