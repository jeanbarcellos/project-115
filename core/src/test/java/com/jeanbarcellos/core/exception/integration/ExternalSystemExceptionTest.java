package com.jeanbarcellos.core.exception.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Testes unitários da classe {@link ExternalSystemException}.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class ExternalSystemExceptionTest {

    @Test
    void constructor_whenAllArgumentsProvided_shouldCreateExternalSystemException() {

        // Arrange
        String service = "legacy-mainframe";
        String message = "Mainframe unavailable";

        // Act
        ExternalSystemException result =
                new ExternalSystemException(
                        service,
                        message,
                        Map.of("host", "legacy01"),
                        TestExternalErrorType.GENERIC,
                        null);

        // Assert
        assertEquals(service, result.getService());
        assertEquals(message, result.getMessage());
        assertEquals("legacy01", result.getMetadata().get("host"));
    }
}