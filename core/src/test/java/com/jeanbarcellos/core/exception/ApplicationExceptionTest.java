package com.jeanbarcellos.core.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/**
 * Testes unitários da classe {@link ApplicationException}.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class ApplicationExceptionTest {

    @Test
    void constructor_whenMessageIsProvided_shouldCreateException() {

        // Arrange
        String message = "Unexpected error";

        // Act
        ApplicationException result = new ApplicationException(message);

        // Assert
        assertEquals(message, result.getMessage());
    }

    @Test
    void constructor_whenMessageAndCauseAreProvided_shouldCreateException() {

        // Arrange
        String message = "Unexpected error";
        Throwable cause = new RuntimeException("Root cause");

        // Act
        ApplicationException result = new ApplicationException(message, cause);

        // Assert
        assertEquals(message, result.getMessage());
        assertSame(cause, result.getCause());
    }
}