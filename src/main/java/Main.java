import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Path;

import classes.AppConfig;
import classes.CsvParser;
import classes.TestCase;
import classes.TestStep;
import classes.TestClassParser;

public class Main {
    private static final AppConfig config = AppConfig.getInstance();
    private static String currentReportDir;

    private static void initializeReportDirectory() {
        // Create timestamp-based directory name
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(config.getTimestampFormat());
        String timestamp = now.format(formatter);
        currentReportDir = config.getReportsDir() + "/test_report_" + timestamp;

        try {
            // Create directories
            Files.createDirectories(Path.of(currentReportDir));
            System.out.println("Created report directory: " + currentReportDir);
        } catch (IOException e) {
            System.err.println("Error creating directories: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        initializeReportDirectory();
        // Process CSV test cases
        processTestCases();
        
        // Process test descriptions from Java files
        processTestDescriptions();
        
        // Compare and categorize tests
        compareAndCategorizeTests();
    }

    private static void processTestCases() {
        try {
            // Create CsvParser instance
            CsvParser parser = new CsvParser();
            
            // Parse CSV and get list of TestCases
            List<TestCase> testCases = parser.parseTestCases();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(currentReportDir + "/output.txt"))) {
                writer.println("Test Cases:");
                
                for (TestCase test : testCases) {
                    writer.println("\nTest ID: " + test.id());
                    writer.println("Title: " + test.title());
                    writer.println("Section: " + test.section());
                    writer.println("Priority: " + test.priority());
                    writer.println("Type: " + test.type());
                    writer.println("Preconditions: " + test.preconditions());
                    writer.println("Is Automated: " + test.isAutomated());
                    writer.println("Automation iOS: " + test.automationIOS());
                    writer.println("Steps: ");
                    for (TestStep step : test.steps()) {
                        writer.println("  - Description: " + step.description());
                        writer.println("    Expected: " + step.expectedResult());
                    }
                    writer.println("------------------------");
                }
                System.out.println("Test cases have been written to output.txt");
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }
        } catch (IllegalStateException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Please check the csv.path property in config.properties");
            System.exit(1);
        }
    }

    private static void processTestDescriptions() {
        // Create TestClassParser instance
        TestClassParser parser = new TestClassParser();
        
        // Parse test descriptions
        List<String> testDescriptions = parser.parseTestDescriptions();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(currentReportDir + "/test_descriptions.txt"))) {
            writer.println("Test Descriptions:");
            
            for (int i = 0; i < testDescriptions.size(); i++) {
                writer.println("\nTest " + (i + 1) + ":");
                writer.println(testDescriptions.get(i));
                writer.println("------------------------");
            }
            System.out.println("Test descriptions have been written to test_descriptions.txt");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private static void compareAndCategorizeTests() {
        try {
            // Read test cases from output.txt
            Map<String, TestCase> testCases = new HashMap<>();
            List<String> lines = Files.readAllLines(Paths.get(currentReportDir + "/output.txt"));
            String currentId = null;
            String currentTitle = null;
            String currentIsAutomated = null;
            String currentAutomationIOS = null;

            for (String line : lines) {
                if (line.startsWith("Test ID: ")) {
                    currentId = line.substring("Test ID: ".length()).trim();
                } else if (line.startsWith("Title: ")) {
                    currentTitle = line.substring("Title: ".length()).trim();
                } else if (line.startsWith("Is Automated: ")) {
                    currentIsAutomated = line.substring("Is Automated: ".length()).trim();
                } else if (line.startsWith("Automation iOS: ")) {
                    currentAutomationIOS = line.substring("Automation iOS: ".length()).trim();
                } else if (line.startsWith("------------------------")) {
                    if (currentId != null && currentTitle != null) {
                        testCases.put(currentTitle, new TestCase(
                            currentId,                  // id
                            currentTitle,               // title
                            "",                        // androidAutomated
                            currentAutomationIOS,      // automationIOS
                            "",                        // commentsAndFeedback
                            "",                        // createdBy
                            "",                        // createdOn
                            "",                        // estimate
                            "",                        // expectedResultDoNotUse
                            "",                        // forecast
                            currentIsAutomated,        // isAutomated
                            "",                        // manualQAReviewNeeded
                            "",                        // milestone
                            "",                        // preconditions
                            "",                        // priority
                            "",                        // readyForAutomation
                            "",                        // references
                            "",                        // region
                            "",                        // section
                            "",                        // sectionDepth
                            "",                        // sectionDescription
                            "",                        // sectionHierarchy
                            "",                        // stepsAdditionalInfo
                            "",                        // stepsDoNotUse
                            "",                        // stepsReferences
                            "",                        // stepsSharedStepId
                            "",                        // stepsStep
                            "",                        // suite
                            "",                        // suiteId
                            "",                        // template
                            "",                        // testRunType
                            "",                        // type
                            "",                        // updatedBy
                            "",                        // updatedOn
                            currentAutomationIOS,      // iOSAutomated
                            List.<TestStep>of()        // steps
                        ));
                    }
                    currentId = null;
                    currentTitle = null;
                    currentIsAutomated = null;
                    currentAutomationIOS = null;
                }
            }

            // Read test descriptions from test_descriptions.txt
            Set<String> automatedTests = new HashSet<>(Files.readAllLines(Paths.get(currentReportDir + "/test_descriptions.txt")));

            // Create lists for automated and non-automated tests
            List<TestCase> automatedTestsList = new ArrayList<>();
            List<TestCase> nonAutomatedTestsList = new ArrayList<>();

            // Compare and categorize
            for (Map.Entry<String, TestCase> entry : testCases.entrySet()) {
                TestCase testCase = entry.getValue();
                boolean isAutomatedInCode = false;
                
                for (String automatedTest : automatedTests) {
                    if (testCase.title().trim().equalsIgnoreCase(automatedTest.trim())) {
                        automatedTestsList.add(testCase);
                        isAutomatedInCode = true;
                        break;
                    }
                }
                if (!isAutomatedInCode) {
                    nonAutomatedTestsList.add(testCase);
                }
            }

            // Write automated tests to file
            try (PrintWriter writer = new PrintWriter(new FileWriter(currentReportDir + "/automated_tests.txt"))) {
                writer.println("Automated Tests (" + automatedTestsList.size() + " tests):");
                for (TestCase test : automatedTestsList) {
                    writer.println("\nTest ID: " + test.id());
                    writer.println("Title: " + test.title());
                    writer.println("Is Automated: " + test.isAutomated());
                    writer.println("Automation iOS: " + test.automationIOS());
                    writer.println("------------------------");
                }
                System.out.println("Automated tests have been written to automated_tests.txt");
            }

            // Write non-automated tests to file
            try (PrintWriter writer = new PrintWriter(new FileWriter(currentReportDir + "/non_automated_tests.txt"))) {
                writer.println("Non-Automated Tests (" + nonAutomatedTestsList.size() + " tests):");
                for (TestCase test : nonAutomatedTestsList) {
                    writer.println("\nTest ID: " + test.id());
                    writer.println("Title: " + test.title());
                    writer.println("Is Automated: " + test.isAutomated());
                    writer.println("Automation iOS: " + test.automationIOS());
                    writer.println("------------------------");
                }
                System.out.println("Non-automated tests have been written to non_automated_tests.txt");
            }

        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
        }
    }
}
