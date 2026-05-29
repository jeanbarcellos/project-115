package com.jeanbarcellos.core.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class ValidationErrorTest {

    @Test
    void of_whenRejectedValueIsNotProvided_shouldCreateValidationErrorWithoutRejectedValue() {

        // Arrange
        String field = "cpf";
        String message = "Invalid CPF";

        // Act
        ValidationError result = ValidationError.of(field, message);

        // Assert
        assertEquals(field, result.getField());
        assertEquals(message, result.getMessage());
        assertNull(result.getRejectedValue());
    }

    @Test
    void of_whenRejectedValueIsProvided_shouldCreateValidationErrorWithRejectedValue() {

        // Arrange
        String field = "cpf";
        String message = "Invalid CPF";
        String rejectedValue = "123";

        // Act
        ValidationError result = ValidationError.of(field, message, rejectedValue);

        // Assert
        assertEquals(field, result.getField());
        assertEquals(message, result.getMessage());
        assertEquals(rejectedValue, result.getRejectedValue());
    }
}