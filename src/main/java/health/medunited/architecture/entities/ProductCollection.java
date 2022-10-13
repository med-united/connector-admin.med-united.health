package health.medunited.architecture.entities;

import health.medunited.architecture.odata.annotations.ODataCacheControl;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@ODataCacheControl(maxAge = 5) // this odata entity set should be cached for 5 seconds
public class ProductCollection {

    @Id
    private String id = UUID.randomUUID().toString();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
