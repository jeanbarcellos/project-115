package com.jeanbarcellos.core.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.jeanbarcellos.core.error.TechnicalErrorType;

/**
 * Testes unitários da classe {@link BusinessException}.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class BusinessExceptionTest {

    @Test
    void constructor_whenOnlyMessageIsProvided_shouldCreateExceptionWithEmptyProperties() {

        // Arrange
        var errorType = TechnicalErrorType.RESOURCE_NOT_FOUND;
        String message = "User not found";

        // Act
        BusinessException result = new BusinessException(errorType, message);

        // Assert
        assertEquals(errorType, result.getType());
        assertEquals(message, result.getMessage());
        assertTrue(result.getProperties().isEmpty());
    }

    @Test
    void constructor_whenPropertiesAreProvided_shouldCreateExceptionWithProperties() {

        // Arrange
        var errorType = TechnicalErrorType.RESOURCE_NOT_FOUND;
        String message = "User not found";
        Map<String, Object> properties = Map.of("userId", 123L);

        // Act
        BusinessException result =
                new BusinessException(
                        errorType,
                        message,
                        properties);

        // Assert
        assertEquals(errorType, result.getType());
        assertEquals(message, result.getMessage());
        assertEquals(properties, result.getProperties());
    }
}