package com.jeanbarcellos.core.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Testes de unidade da classe {@link DomainException}.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class DomainExceptionTest {

    @Test
    void constructor_whenOnlyMessageIsProvided_shouldCreateExceptionWithEmptyContext() {

        // Arrange
        String message = "Wallet is blocked";

        // Act
        DomainException result = new DomainException(message);

        // Assert
        assertEquals(message, result.getMessage());
        assertTrue(result.getContext().isEmpty());
    }

    @Test
    void constructor_whenContextIsProvided_shouldCreateExceptionWithContext() {

        // Arrange
        String message = "Wallet is blocked";
        Map<String, Object> context = Map.of("walletId", 100L);

        // Act
        DomainException result =
                new DomainException(
                        message,
                        context);

        // Assert
        assertEquals(message, result.getMessage());
        assertEquals(context, result.getContext());
    }
}