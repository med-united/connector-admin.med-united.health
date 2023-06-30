package health.medunited.architecture.jaxrs.resource;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import java.io.*;
import java.util.logging.Logger;

import health.medunited.architecture.monitoring.Scheduler;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


public class MonitoringRequestTest {
    @Inject
    MonitoringRequest monitoringConfiguration;

    private static final Logger log = Logger.getLogger(Scheduler.class.getName());

    @Test
    @Disabled
    void testWriteFile() throws IOException {
        monitoringConfiguration = new MonitoringRequest(true, true, false);

        JsonbConfig config = new JsonbConfig().withFormatting(true);
        Jsonb jsonb = JsonbBuilder.newBuilder().withConfig(config).build();

        String serialized = jsonb.toJson(monitoringConfiguration);
        FileWriter writer = new FileWriter("./monitoring/MonitoringAspects.json");
        writer.write(serialized);
        writer.close();

    }

    @Test
    @Disabled
    void testReadFile() throws IOException {

        Jsonb jsonb = JsonbBuilder.create();
        FileReader reader = new FileReader("./monitoring/MonitoringAspects.json");
        MonitoringRequest incomingMonitoring = jsonb.fromJson(reader, MonitoringRequest.class);
        log.info("Reading a value from successful deserialization (can be true or false): "+incomingMonitoring.isCheckTIStatusOnlineOn());

    }

}

