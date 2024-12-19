package com.project.bricks;
import com.project.bricks.models.placeholders.Prefix;
import org.junit.Test;
import static org.junit.Assert.*;
public class PrefixTest {
    @Test
    public void testValidPrefixWithoutParams() {
        // Arrange & Act
        Prefix prefix = new Prefix("{{testKey}}");

        // Assert
        assertEquals("testKey", prefix.getKey());
        assertEquals(0, prefix.getParams().length);
        assertEquals("{{", prefix.getLeftSideIndicator());
        assertEquals("}}", prefix.getRightSideIndicator());
    }

    @Test
    public void testValidPrefixWithParams() {
        // Arrange & Act
        Prefix prefix = new Prefix("{{testKey:param1,param2,param3}}");

        // Assert
        assertEquals("testKey", prefix.getKey());
        assertArrayEquals(new String[]{"param1", "param2", "param3"}, prefix.getParams());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPrefixMissingLeftIndicator() {
        new Prefix("testKey}}");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPrefixMissingRightIndicator() {
        new Prefix("{{testKey");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPrefixNoIndicators() {
        new Prefix("testKey");
    }

    @Test
    public void testToStringMethod() {
        // Arrange
        Prefix prefix = new Prefix("{{testKey}}");

        // Act
        String result = prefix.toString();

        // Assert
        assertEquals("{{testKey}}", result);
    }

    @Test
    public void testPrefixWithEmptyParams() {
        // Arrange & Act
        Prefix prefix = new Prefix("{{testKey}}");

        // Assert
        assertEquals("testKey", prefix.getKey());
        assertEquals(0, prefix.getParams().length);
    }

    @Test
    public void testPrefixWithSingleParam() {
        // Arrange & Act
        Prefix prefix = new Prefix("{{testKey:singleParam}}");

        // Assert
        assertEquals("testKey", prefix.getKey());
        assertEquals(1, prefix.getParams().length);
        assertEquals("singleParam", prefix.getParams()[0]);
    }


}
