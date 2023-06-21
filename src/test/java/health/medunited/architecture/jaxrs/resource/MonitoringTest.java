package health.medunited.architecture.jaxrs.resource;

import javax.inject.Inject;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MonitoringTest {
    @Inject
    Monitoring monitoring;

    @Test
    @Disabled
    void testWriteFile() {
        monitoring = new Monitoring();
        monitoring.writeFile();
    }

}
