package com.jeanbarcellos.core.exception.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.jeanbarcellos.core.error.ErrorType;
import com.jeanbarcellos.core.error.ExternalErrorType;
import com.jeanbarcellos.core.error.TechnicalErrorType;

/**
 * Testes de unidade da classe {@link IntegrationException}.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class IntegrationExceptionTest {

    @Test
    void constructor_whenAllArgumentsProvided_shouldCreateIntegrationException() {

        // Arrange
        String service = "rest-serpro";
        String message = "Provider error";

        Throwable cause = new RuntimeException("Root cause");

        Map<String, Object> metadata = Map.of(
                "requestId", "123");

        // Act
        IntegrationException result = new TestIntegrationException(
                service,
                message,
                metadata,
                TestExternalErrorType.GENERIC,
                cause);

        // Assert
        assertEquals(service, result.getService());
        assertEquals(message, result.getMessage());

        assertEquals(metadata, result.getMetadata());

        assertSame(
                TestExternalErrorType.GENERIC,
                result.getExternalError());

        assertSame(cause, result.getCause());
    }

    @Test
    void constructor_whenMetadataIsNull_shouldCreateEmptyMetadataMap() {

        // Arrange

        // Act
        IntegrationException result = new TestIntegrationException(
                "rest-serpro",
                "Provider error",
                null,
                TestExternalErrorType.GENERIC,
                null);

        // Assert
        assertTrue(result.getMetadata().isEmpty());
    }

    @Test
    void constructor_whenMetadataIsProvided_shouldCreateImmutableMetadataMap() {

        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("requestId", "123");

        // Act
        IntegrationException result = new TestIntegrationException(
                "rest-serpro",
                "Provider error",
                metadata,
                TestExternalErrorType.GENERIC,
                null);

        // Assert
        assertThrows(
                UnsupportedOperationException.class,
                () -> result.getMetadata().put("newKey", "newValue"));
    }

    @Test
    void getErrorType_whenExternalErrorIsProvided_shouldReturnMappedErrorType() {

        // Arrange
        IntegrationException exception = new TestIntegrationException(
                "rest-serpro",
                "Provider error",
                null,
                TestExternalErrorType.GENERIC,
                null);

        // Act
        ErrorType result = exception.getErrorType();

        // Assert
        assertEquals(
                TestExternalErrorType.GENERIC.getErrorType(),
                result);
    }

    @Test
    void getErrorType_whenExternalErrorIsNull_shouldReturnDefaultErrorType() {

        // Arrange
        IntegrationException exception = new TestIntegrationException(
                "rest-serpro",
                "Provider error",
                null,
                null,
                null);

        // Act
        ErrorType result = exception.getErrorType();

        // Assert
        assertEquals(
                TechnicalErrorType.EXTERNAL_SERVICE_ERROR,
                result);
    }

    /**
     * Implementação concreta utilizada exclusivamente
     * nos Testes de unidade de {@link IntegrationException}.
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
     */
    private static class TestIntegrationException extends IntegrationException {

        TestIntegrationException(
                String service,
                String message,
                Map<String, Object> metadata,
                ExternalErrorType externalError,
                Throwable cause) {

            super(
                    service,
                    message,
                    metadata,
                    externalError,
                    cause);
        }

    }
}