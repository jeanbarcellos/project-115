package com.jeanbarcellos.core.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.jeanbarcellos.core.error.DomainViolation;

/**
 * Testes unitários da classe {@link DomainValidationException}.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class DomainValidationExceptionTest {

    @Test
    void constructor_whenViolationsAreNull_shouldCreateExceptionWithEmptyList() {

        // Arrange
        String message = "Invalid domain";

        // Act
        DomainValidationException result =
                new DomainValidationException(
                        message,
                        null);

        // Assert
        assertEquals(message, result.getMessage());
        assertTrue(result.getViolations().isEmpty());
    }

    @Test
    void constructor_whenViolationsAreProvided_shouldCreateExceptionWithViolations() {

        // Arrange
        String message = "Invalid domain";

        List<DomainViolation> violations = List.of(
                DomainViolation.of(
                        "balance",
                        "must be positive"));

        // Act
        DomainValidationException result =
                new DomainValidationException(
                        message,
                        violations);

        // Assert
        assertEquals(message, result.getMessage());
        assertEquals(violations, result.getViolations());
    }
}