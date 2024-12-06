package classes;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {
    private final AppConfig config = AppConfig.getInstance();
    private final String csvFilePath;
    
    public CsvParser() {
        this.csvFilePath = config.getCsvPath();
    }
    
    public static String cleanString(String value) {
        if (value == null)
            return "";
        return value.replace("\"", "").trim();
    }
    
    public List<TestCase> parseTestCases() {
        List<TestCase> testCases = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            List<String[]> rows = reader.readAll();
            // Skip header row
            if (rows.size() > 1) rows.remove(0);
            
            for (String[] row : rows) {
                testCases.add(new TestCase(
                    cleanString(row[0]),
                    cleanString(row[1]),
                    cleanString(row[2]),
                    cleanString(row[3]),
                    cleanString(row[4]),
                    cleanString(row[5]),
                    cleanString(row[6]),
                    cleanString(row[7]),
                    cleanString(row[8]),
                    cleanString(row[9]),
                    cleanString(row[10]),
                    cleanString(row[11]),
                    cleanString(row[12]),
                    cleanString(row[13]),
                    cleanString(row[14]),
                    cleanString(row[15]),
                    cleanString(row[16]),
                    cleanString(row[17]),
                    cleanString(row[18]),
                    cleanString(row[19]),
                    cleanString(row[20]),
                    cleanString(row[21]),
                    cleanString(row[23]),
                    cleanString(row[24]),
                    cleanString(row[26]),
                    cleanString(row[27]),
                    cleanString(row[28]),
                    cleanString(row[29]),
                    cleanString(row[30]),
                    cleanString(row[31]),
                    cleanString(row[32]),
                    cleanString(row[33]),
                    cleanString(row[34]),
                    cleanString(row[35]),
                    cleanString(row[36]),
                    parseSteps(row[22])
                ));
            }
            
        } catch (IOException | CsvException e) {
            System.err.println("Error parsing CSV file: " + e.getMessage());
        }
        
        return testCases;
    }
   
    public static List<TestStep> parseSteps(String stepsContent) {
        List<TestStep> steps = new ArrayList<>();
        if (stepsContent == null || stepsContent.isEmpty()) {
            return steps;
        }

        String[] lines = stepsContent.split("\n");
        StringBuilder currentStep = new StringBuilder();
        StringBuilder currentExpectedResult = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.startsWith("Expected Result:")) {
                // Collect expected result lines until next step or end
                i++;
                while (i < lines.length && !lines[i].endsWith("Expected Result:")) {
                    currentExpectedResult.append(lines[i]).append("\n");
                    i++;
                }
                i--; // Adjust for loop increment

                // Create new TestStep
                steps.add(new TestStep(
                        currentStep.toString().trim(),
                        currentExpectedResult.toString().trim()));

                // Reset builders
                currentStep = new StringBuilder();
                currentExpectedResult = new StringBuilder();
            } else if (!line.isEmpty()) {
                currentStep.append(line).append("\n");
            }
        }
        return steps;
    }
}
