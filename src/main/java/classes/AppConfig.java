package classes;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final String CONFIG_FILE = "config.properties";
    private static AppConfig instance;
    private final Properties properties;

    private AppConfig() {
        properties = new Properties();
        loadProperties();
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IOException("Unable to find " + CONFIG_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading configuration: " + e.getMessage(), e);
        }
    }

    // Directory configurations
    public String getReportsDir() {
        return properties.getProperty("reports.dir");
    }

    public String getReportsOutputFile() {
        return properties.getProperty("reports.output_file");
    }

    public String getTestDescriptionsFile() {
        return properties.getProperty("reports.test_descriptions_file");
    }

    public String getAutomatedTestsFile() {
        return properties.getProperty("reports.automated_tests_file");
    }

    public String getNonAutomatedTestsFile() {
        return properties.getProperty("reports.non_automated_tests_file");
    }

    // CSV configuration
    public String getCsvPath() {
        String path = properties.getProperty("csv.path");
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalStateException("CSV path is not configured in config.properties");
        }
        return path;
    }

    // Application settings
    public String getTimestampFormat() {
        return properties.getProperty("app.timestamp_format");
    }

    // Test Classes configuration
    public String getTestClassesPath() {
        String path = properties.getProperty("test.classes.path");
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalStateException("Test classes path is not configured in config.properties");
        }
        return path;
    }
} 