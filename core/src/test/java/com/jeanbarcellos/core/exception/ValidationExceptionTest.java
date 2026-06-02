package com.jeanbarcellos.core.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.jeanbarcellos.core.error.ValidationError;

/**
 * Testes de unidade da classe {@link ValidationException}.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class ValidationExceptionTest {

    @Test
    void constructor_whenErrorsAreNull_shouldCreateExceptionWithEmptyList() {

        // Arrange
        String message = "Validation failed";

        // Act
        ValidationException result = new ValidationException(
                message,
                (List<ValidationError>) null);

        // Assert
        assertEquals(message, result.getMessage());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void constructor_whenErrorListIsProvided_shouldCreateExceptionWithErrors() {

        // Arrange
        String message = "Validation failed";

        List<ValidationError> errors = List.of(
                ValidationError.of(
                        "cpf",
                        "invalid cpf"));

        // Act
        ValidationException result = new ValidationException(
                message,
                errors);

        // Assert
        assertEquals(message, result.getMessage());
        assertEquals(errors, result.getErrors());
    }

    @Test
    void constructor_whenSingleErrorIsProvided_shouldCreateExceptionWithOneError() {

        // Arrange
        String message = "Validation failed";

        ValidationError error = ValidationError.of(
                "cpf",
                "invalid cpf");

        // Act
        ValidationException result = new ValidationException(
                message,
                error);

        // Assert
        assertEquals(message, result.getMessage());
        assertEquals(1, result.getErrors().size());
        assertEquals(error, result.getErrors().get(0));
    }

    @Test
    void constructor_whenSingleErrorIsNull_shouldCreateExceptionWithEmptyErrorList() {

        // Arrange
        String message = "Validation failed";

        // Act
        ValidationException result = new ValidationException(
                message,
                (ValidationError) null);

        // Assert
        assertEquals(message, result.getMessage());
        assertTrue(result.getErrors().isEmpty());
    }
}