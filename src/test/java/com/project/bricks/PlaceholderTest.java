package com.project.bricks;

import com.project.bricks.models.placeholders.Placeholder;
import com.project.bricks.models.placeholders.Prefix;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PlaceholderTest {

    private static final String TEST_KEY = "testKey";
    private static final String TEST_NAME = "Test Name";
    private static final String TEST_DESCRIPTION = "Test Description";
    private static final String TEST_DEFAULT_VALUE = "default";
    private static final boolean TEST_REQUIRED = true;

    private Placeholder placeholder;

    @Before
    public void setUp() {
        placeholder = new Placeholder(TEST_KEY, TEST_NAME, TEST_DESCRIPTION,
                TEST_DEFAULT_VALUE, TEST_REQUIRED);
    }

    @Test
    public void testConstructor() {
        // Assert
        assertEquals(TEST_KEY, placeholder.getKey());
        assertEquals(TEST_NAME, placeholder.getName());
        assertEquals(TEST_DESCRIPTION, placeholder.getDescription());
        assertEquals(TEST_DEFAULT_VALUE, placeholder.getDefaultValue());
        assertEquals(TEST_REQUIRED, placeholder.isRequired());
        assertNotNull(placeholder.getPrefix());
        assertEquals("{{" + TEST_KEY + "}}", placeholder.getPrefix().toString());
    }

    @Test
    public void testGenerateWithProvidedValue() {
        // Arrange
        String testValue = "test value";

        // Act
        Object result = placeholder.generate(testValue);

        // Assert
        assertEquals(testValue, result);
        assertEquals(testValue, placeholder.getValue());
    }

    @Test
    public void testGenerateWithDefaultValue() {
        // Act
        Object result = placeholder.generate(null);

        // Assert
        assertEquals(TEST_DEFAULT_VALUE, result);
        assertEquals(TEST_DEFAULT_VALUE, placeholder.getValue());
    }

    @Test
    public void testGenerateWithCustomTransformation() {
        // Arrange
        placeholder.setExtendedTransform(value -> value.toString().toUpperCase());
        String testValue = "test value";

        // Act
        Object result = placeholder.generate(testValue);

        // Assert
        assertEquals("TEST VALUE", result);
        assertEquals("TEST VALUE", placeholder.getValue());
    }

    @Test
    public void testGenerateWithValidation_ValidValue() {
        // Arrange
        placeholder.setValidate(value -> value.toString().length() > 3);
        String testValue = "valid value";

        // Act
        Object result = placeholder.generate(testValue);

        // Assert
        assertEquals(testValue, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerateWithValidation_InvalidValue() {
        // Arrange
        placeholder.setValidate(value -> value.toString().length() > 3);
        String testValue = "no";

        // Act
        placeholder.generate(testValue);
    }

    @Test
    public void testTransformWithOptions() {
        // Arrange
        Prefix prefixWithParams = new Prefix("{{" + TEST_KEY + ":uppercase}}");
        placeholder.setPrefix(prefixWithParams);
        String testValue = "test value";

        // Act
        Object result = placeholder.generate(testValue);

        // Assert
        assertEquals("TEST VALUE", result);
    }

    @Test
    public void testTransformWithMultipleOptions() {
        // Arrange
        Prefix prefixWithParams = new Prefix("{{" + TEST_KEY + ":uppercase,trim}}");
        placeholder.setPrefix(prefixWithParams);
        String testValue = "  test value  ";

        // Act
        Object result = placeholder.generate(testValue);

        // Assert
        assertEquals("TEST VALUE", result);
    }

    @Test
    public void testCombinedCustomTransformAndOptions() {
        // Arrange
        Prefix prefixWithParams = new Prefix("{{" + TEST_KEY + ":uppercase}}");
        placeholder.setPrefix(prefixWithParams);
        placeholder.setExtendedTransform(value -> value.toString() + "_CUSTOM");
        String testValue = "test";

        // Act
        Object result = placeholder.generate(testValue);

        // Assert
        assertEquals("TEST_CUSTOM", result);
    }


}
