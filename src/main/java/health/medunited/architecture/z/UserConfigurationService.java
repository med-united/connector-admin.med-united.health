package health.medunited.architecture.z;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

@ApplicationScoped
public class UserConfigurationService {

    final Logger log = Logger.getLogger(getClass().getName());

    private String getConfigFilePath() {
        // TODO configure proper file path
        return "user.properties";
    }

    public Properties getProperties() {
        File file = new File(getConfigFilePath());
        if (!file.exists())
            createConfigurationFile(file);
        if (file.exists())
            return readFile(file);
        else
            return null;
    }

    private void createConfigurationFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            log.severe("Could not create missing configuration file");
        }
    }

    private Properties readFile(File file) {
        final Properties properties = new Properties();
        try {
            properties.load(new FileReader(file));
        } catch (IOException e) {
            log.warning("Failed to load user-config file");
        }
        return properties;
    }

    private void writeFile(File file, Properties properties) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            properties.store(outputStream, "");
            outputStream.close();
        } catch (IOException e) {
            log.severe("Could not store configurations");
        }
    }

    private void updateConfig(Properties updated) {
        File file = new File(getConfigFilePath());
        if (!file.exists())
            createConfigurationFile(file);
        Properties properties = readFile(file);
        properties.putAll(updated);
        writeFile(file, properties);
    }

    public UserConfigurations getConfig() {
        Properties properties = getProperties();
        UserConfigurations config = new UserConfigurations(properties);
        return config;
    }

}