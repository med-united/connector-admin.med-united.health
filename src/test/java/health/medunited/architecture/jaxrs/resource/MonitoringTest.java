package health.medunited.architecture.jaxrs.resource;

import javax.inject.Inject;

import java.io.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


public class MonitoringTest {
    @Inject
    Monitoring monitoringConfiguration;

    @Test
    @Disabled
    void testWriteFile() throws IOException {
        monitoringConfiguration = new Monitoring(true, true, false);

        FileWriter writer = new FileWriter("./monitoring/MonitoringAspects.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(monitoringConfiguration, writer);
        writer.close();


    }

}

