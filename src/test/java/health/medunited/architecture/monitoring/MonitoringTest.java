package health.medunited.architecture.monitoring;

import javax.inject.Inject;

import health.medunited.architecture.monitoring.Monitoring;
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
