package classes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestClassParser {
    private static final Pattern TEST_PATTERN = Pattern.compile("@Test\\s*\\(\\s*description\\s*=\\s*\"([^\"]*)\"[^)]*\\)");
    private final AppConfig config;
    
    public TestClassParser() {
        this.config = AppConfig.getInstance();
    }

    public List<String> parseTestDescriptions() {
        List<String> testDescriptions = new ArrayList<>();
        String testClassesPath = config.getTestClassesPath();
        
        try {
            Files.walk(Paths.get(testClassesPath))
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(path -> processFile(path, testDescriptions));
        } catch (IOException e) {
            System.err.println("Error walking through files: " + e.getMessage());
        }
        return testDescriptions;
    }

    private void processFile(Path filePath, List<String> testDescriptions) {
        try {
            String content = Files.readString(filePath);
            Matcher matcher = TEST_PATTERN.matcher(content);
            
            while (matcher.find()) {
                String description = matcher.group(1);
                testDescriptions.add(description);
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + filePath + ": " + e.getMessage());
        }
    }
}
