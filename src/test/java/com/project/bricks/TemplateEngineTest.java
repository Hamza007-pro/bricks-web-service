package com.project.bricks;

import com.project.bricks.models.placeholders.Placeholder;
import com.project.bricks.modules.templateEngine.TemplateEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class TemplateEngineTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private TemplateEngine templateEngine;
    private File templateFile;

    @Before
    public void setUp() throws IOException {
        templateEngine = new TemplateEngine();
        setupTemplateFile();
    }

    private void setupTemplateFile() throws IOException {
        templateFile = tempFolder.newFile("UserTemplate.txt");
        String templateContent =
                "{{>header}}\n\n" +
                        "{{#if isAdmin}}\n" +
                        "[Admin Section]\n" +
                        "{{/if}}\n\n" +
                        "Welcome, {{userName}}!\n" +
                        "Date: {{currentDate}}";

        Files.writeString(templateFile.toPath(), templateContent);
    }

    @Test
    public void testTemplateEngineWithDynamicPlaceholders() {
        // Arrange
        // Set up placeholders
        List<Placeholder> placeholders = Arrays.asList(
                createUserNamePlaceholder(),
                createCurrentDatePlaceholder(),
                createIsAdminPlaceholder()
        );

        // Register partial
        templateEngine.registerPartial("header",
                "// Generated by Template Engine\n" +
                        "// Do not edit directly."
        );

        // Act
        // Process template
        String processedContent = templateEngine.processTemplate(
                templateFile.getAbsolutePath(),
                placeholders
        );

        // Assert
        String expectedOutput = String.format(
                "// Generated by Template Engine\n" +
                        "// Do not edit directly.\n\n" +
                        "[Admin Section]\n\n" +
                        "Welcome, DEFAULTUSER!\n" +
                        "Date: %s",
                LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        );

        assertEquals(
                removeAllWhitespace(expectedOutput.trim()),
                removeAllWhitespace(processedContent.trim())
        );
    }

    @Test
    public void testPartialApplication() {
        // Arrange
        templateEngine.registerPartial("header", "HEADER CONTENT");
        String template = "Start {{>header}} End";

        // Act
        String result = templateEngine.applyPartials(template);

        // Assert
        assertEquals("Start HEADER CONTENT End", result);
    }

    @Test
    public void testConditionalProcessing() {
        // Arrange
        List<Placeholder> placeholders = Arrays.asList(
                new Placeholder("showSection", "Show Section", "Control section visibility", true, true)
        );
        String template = "Start {{#if showSection}}Visible Content{{/if}} End";

        // Act
        String result = templateEngine.applyConditionals(template, placeholders);

        // Assert
        assertEquals("Start Visible Content End", result);
    }

    @Test
    public void testPlaceholderReplacement() {
        // Arrange
        List<Placeholder> placeholders = Arrays.asList(
                new Placeholder("testKey", "Test Key", "Test Description", "testValue", true)
        );
        String template = "Value: {{testKey}}";

        // Act
        String result = templateEngine.applyPlaceholders(template, placeholders);

        // Assert
        assertEquals("Value: testValue", result);
    }

    @Test
    public void testSaveToFile() throws IOException {
        // Arrange
        String content = "Test Content";
        String fileName = "test_template_output.txt";

        // Get user's desktop path
        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";

        // Create a specific folder for our test outputs on desktop
        String outputFolderName = "template-engine-tests";
        String fullOutputPath = desktopPath + File.separator + outputFolderName;

        try {
            // Act
            templateEngine.saveToFile(fullOutputPath, fileName, content);

            // Assert
            File savedFile = new File(fullOutputPath, fileName);
            assertTrue("File should exist at: " + savedFile.getAbsolutePath(), savedFile.exists());
            String savedContent = Files.readString(savedFile.toPath());
            assertEquals("File content should match", content, savedContent);

            // Print the file location for easy access
            System.out.println("Test file saved at: " + savedFile.getAbsolutePath());

        } catch (IOException e) {
            fail("Failed to save or read file: " + e.getMessage());
        }
    }

    // Helper methods
    private Placeholder createUserNamePlaceholder() {
        Placeholder placeholder = new Placeholder(
                "userName",
                "User Name",
                "The user's name",
                "defaultUser",
                true
        );
        placeholder.setExtendedTransform(value -> value.toString().toUpperCase());
        return placeholder;
    }

    private Placeholder createCurrentDatePlaceholder() {
        return new Placeholder(
                "currentDate",
                "Current Date",
                "The current date",
                LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                true
        );
    }

    private Placeholder createIsAdminPlaceholder() {
        return new Placeholder(
                "isAdmin",
                "Is Admin",
                "Admin access flag",
                "true",
                true
        );
    }

    private String removeAllWhitespace(String input) {
        return input.replaceAll("\\s+", "");
    }
}
