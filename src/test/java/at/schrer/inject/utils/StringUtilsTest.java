package at.schrer.inject.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void testReverse() {
        // Given
        String input = "Hello";
        // When
        String result = StringUtils.reverse(input);
        // Then
        assertEquals("olleH", result);
    }

    @Test
    void testReverseEmpty() {
        // Given
        String input = "";
        // When
        String result = StringUtils.reverse(input);
        // Then
        assertEquals("", result);
    }

    @Test
    void testReverseNull() {
        // Given
        // When
        String result = StringUtils.reverse(null);
        // Then
        assertNull(result);
    }

    @Test
    void testRemoveChar() {
        // Given
        char target = 'l';
        String input = "Hello";
        // When
        String result = StringUtils.removeChar(target, input);
        // Then
        assertEquals("Heo", result);
    }

    @Test
    void testRemoveCharNotFound() {
        // Given
        char target = 'x';
        String input = "Hello";
        // When
        String result = StringUtils.removeChar(target, input);
        // Then
        assertEquals("Hello", result);
    }

    @Test
    void testRemoveCharEmpty() {
        // Given
        char target = 'l';
        String input = "";
        // When
        String result = StringUtils.removeChar(target, input);
        // Then
        assertEquals("", result);
    }

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

    @Test
    void testIsPalindromeTrue() {
        // Given
        String input = "anna";
        // When
        boolean result = StringUtils.isPalindrome(input);
        // Then
        assertTrue(result);
    }

    @Test
    void testIsPalindromeTrueUneven() {
        // Given
        String input = "anana";
        // When
        boolean result = StringUtils.isPalindrome(input);
        // Then
        assertTrue(result);
    }

    @Test
    void testIsPalindromeTrueUppercase() {
        // Given
        String input = "Anna";
        // When
        boolean result = StringUtils.isPalindrome(input);
        // Then
        assertTrue(result);
    }

    @Test
    void testIsPalindromeFalse() {
        // Given
        String input = "Hello";
        // When
        boolean result = StringUtils.isPalindrome(input);
        // Then
        assertFalse(result);
    }

    @Test
    void testIsPalindromeEmpty() {
        // Given
        String input = "";
        // When
        boolean result = StringUtils.isPalindrome(input);
        // Then
        assertTrue(result);
    }

    @Test
    void testIsPalindromeNull() {
        // Given
        String input = null;
        // When
        boolean result = StringUtils.isPalindrome(input);
        // Then
        assertFalse(result);
    }

    @Test
    void testSortNull() {
        // Given
        String input = null;
        // When
        String result = StringUtils.sort(input);
        // Then
        assertNull(result);
    }

    @Test
    void testSortEmpty() {
        // Given
        String input = "";
        // When
        String result = StringUtils.sort(input);
        // Then
        assertEquals("", result);
    }

    @Test
    void testSort() {
        // Given
        String input = "hEllo";
        // When
        String result = StringUtils.sort(input);
        // Then
        assertEquals("Ehllo", result);
    }

    @Test
    void testSortCase(){
        // Given
        String input = "aAaAaA";
        // When
        String result = StringUtils.sort(input);
        // Then
        assertEquals("AAAaaa", result);
    }

    @Test
    void testSum(){
        // Given
        String first = "15";
        String second = "0";
        // When
        String result = StringUtils.sum(first, second);
        // Then
        assertEquals("15", result);
    }

    @Test
    void testSumNonNum(){
        // Given
        String first = "1a5";
        String second = "0";
        // When/Then
        assertThrows(NumberFormatException.class,
                () -> StringUtils.sum(first, second));
    }

    @Test
    void testSum2(){
        // Given
        String first = "02258754";
        String second = "12";
        // When
        String result = StringUtils.sum(first, second);
        // Then
        assertEquals("2258766", result);
    }

    @Test
    void testSumNegative(){
        // Given
        String first = "5";
        String second = "-12";
        // When
        String result = StringUtils.sum(first, second);
        // Then
        assertEquals("-7", result);
    }

    @Test
    void testLength(){
        // Given
        String input = "5";
        // When
        int result = StringUtils.length(input);
        // Then
        assertEquals(1, result);
    }

    @Test
    void testLengthNull(){
        // Given
        String input = null;
        // When
        int result = StringUtils.length(input);
        // Then
        assertEquals(0, result);
    }

    @Test
    void testLengthEmpty(){
        // Given
        String input = "";
        // When
        int result = StringUtils.length(input);
        // Then
        assertEquals(0, result);
    }

    @Test
    void testLength2(){
        // Given
        String input = "ycxdgc";
        // When
        int result = StringUtils.length(input);
        // Then
        assertEquals(6, result);
    }
}
