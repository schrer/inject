package at.schrer.inject.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void testIsBlankEmpty() {
        // Given
        String input = "";
        // When
        boolean result = StringUtils.isBlank(input);
        // Then
        assertTrue(result);
    }

    @Test
    void testIsBlankNull() {
        // Given
        String input = null;
        // When
        boolean result = StringUtils.isBlank(input);
        // Then
        assertTrue(result);
    }

    @Test
    void testIsBlankFalse() {
        // Given
        String input = "a";
        // When
        boolean result = StringUtils.isBlank(input);
        // Then
        assertFalse(result);
    }

    @Test
    void testIsBlankWhitespace() {
        // Given
        String input = " ";
        // When
        boolean result = StringUtils.isBlank(input);
        // Then
        assertTrue(result);
    }

    @Test
    void testIsEmptyFalse() {
        // Given
        String input = "a";
        // When
        boolean result = StringUtils.isEmpty(input);
        // Then
        assertFalse(result);
    }

    @Test
    void testIsEmptyTrue() {
        // Given
        String input = "";
        // When
        boolean result = StringUtils.isEmpty(input);
        // Then
        assertTrue(result);
    }

    @Test
    void testIsEmptyWhitespace() {
        // Given
        String input = " ";
        // When
        boolean result = StringUtils.isEmpty(input);
        // Then
        assertFalse(result);
    }
}
