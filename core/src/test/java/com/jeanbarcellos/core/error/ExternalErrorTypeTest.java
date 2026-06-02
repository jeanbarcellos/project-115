package com.jeanbarcellos.core.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Testes de unidade do contrato {@link ExternalErrorType}.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class ExternalErrorTypeTest {

    @Test
    void getStatus_whenProviderErrorExists_shouldReturnStatus() {

        // Arrange
        ExternalErrorType error = TestExternalErrorType.GENERIC;

        // Act
        Integer result = error.getStatus();

        // Assert
        assertEquals(500, result);
    }

    @Test
    void getCode_whenProviderErrorExists_shouldReturnCode() {

        // Arrange
        ExternalErrorType error = TestExternalErrorType.GENERIC;

        // Act
        String result = error.getCode();

        // Assert
        assertEquals("GENERIC", result);
    }

    @Test
    void getDescription_whenProviderErrorExists_shouldReturnDescription() {

        // Arrange
        ExternalErrorType error = TestExternalErrorType.GENERIC;

        // Act
        String result = error.getDescription();

        // Assert
        assertEquals(
                "Generic provider error",
                result);
    }

    @Test
    void isRetryable_whenProviderErrorIsRetryable_shouldReturnTrue() {

        // Arrange
        ExternalErrorType error = TestExternalErrorType.GENERIC;

        // Act
        boolean result = error.isRetryable();

        // Assert
        assertTrue(result);
    }

    @Test
    void isRetryable_whenProviderErrorIsNotRetryable_shouldReturnFalse() {

        // Arrange
        ExternalErrorType error = TestExternalErrorType.NOT_FOUND;

        // Act
        boolean result = error.isRetryable();

        // Assert
        assertFalse(result);
    }

    @Test
    void getErrorType_whenProviderErrorIsMapped_shouldReturnInternalErrorType() {

        // Arrange
        ExternalErrorType error = TestExternalErrorType.GENERIC;

        // Act
        ErrorType result = error.getErrorType();

        // Assert
        assertEquals(
                TechnicalErrorType.EXTERNAL_SERVICE_ERROR,
                result);
    }

    @Test
    void getErrorType_whenProviderErrorIsMappedToResourceNotFound_shouldReturnMappedErrorType() {

        // Arrange
        ExternalErrorType error = TestExternalErrorType.NOT_FOUND;

        // Act
        ErrorType result = error.getErrorType();

        // Assert
        assertEquals(
                TechnicalErrorType.RESOURCE_NOT_FOUND,
                result);
    }

    /**
     * Implementação auxiliar utilizada nos testes
     * do contrato {@link ExternalErrorType}.
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
     */
    @Getter
    @RequiredArgsConstructor
    enum TestExternalErrorType implements ExternalErrorType {

        GENERIC(
                500,
                "GENERIC",
                "Generic provider error",
                true,
                TechnicalErrorType.EXTERNAL_SERVICE_ERROR),

        NOT_FOUND(
                404,
                "NOT_FOUND",
                "Resource not found",
                false,
                TechnicalErrorType.RESOURCE_NOT_FOUND);

        private final Integer status;

        private final String code;

        private final String description;

        private final boolean retryable;

        private final ErrorType errorType;

    }
}