package com.jeanbarcellos.core.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class DomainViolationTest {

    @Test
    void of_whenRejectedValueIsNotProvided_shouldCreateViolationWithoutRejectedValue() {

        // Arrange
        String field = "balance";
        String message = "Balance must be positive";

        // Act
        DomainViolation result = DomainViolation.of(field, message);

        // Assert
        assertEquals(field, result.getField());
        assertEquals(message, result.getMessage());
        assertNull(result.getRejectedValue());
    }

    @Test
    void of_whenRejectedValueIsProvided_shouldCreateViolationWithRejectedValue() {

        // Arrange
        String field = "balance";
        String message = "Balance must be positive";
        Integer rejectedValue = -100;

        // Act
        DomainViolation result = DomainViolation.of(field, message, rejectedValue);

        // Assert
        assertEquals(field, result.getField());
        assertEquals(message, result.getMessage());
        assertEquals(rejectedValue, result.getRejectedValue());
    }
}