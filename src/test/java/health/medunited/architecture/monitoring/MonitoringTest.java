
package health.medunited.architecture.monitoring;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;

public class MonitoringTest {
    @Inject
    Monitoring monitoring;

    @Test
    @Disabled
    void testWriteFile() throws IOException {
        monitoring = new Monitoring(22,185);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Writer writer = new FileWriter("./monitoring/MonitoringAspects.json");
        gson.toJson(monitoring, writer);
        writer.flush();
        writer.close();
    }

}

