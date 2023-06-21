package health.medunited.architecture.monitoring;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


public class MonitoringTest {
    @Inject
    Monitoring monitoring;

    @Test
    @Disabled
    void testWriteFile() throws IOException {
        monitoring = new Monitoring(true, true, false);

        ObjectMapper objectMapper = new ObjectMapper();
        monitoring = new Monitoring(true, true, false);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("./monitoring/MonitoringAspects.json"), monitoring);

    }

}

