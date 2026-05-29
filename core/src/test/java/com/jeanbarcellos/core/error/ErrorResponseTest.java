package com.jeanbarcellos.core.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class ErrorResponseTest {

    @Test
    void builder_whenOnlyMandatoryFieldsProvided_shouldCreateMinimalResponse() {

        // Arrange
        String title = "Internal Error";
        String detail = "Unexpected error";

        // Act
        ErrorResponse result = ErrorResponse.builder()
                .title(title)
                .status(500)
                .detail(detail)
                .build();

        // Assert
        assertEquals(title, result.getTitle());
        assertEquals(500, result.getStatus());
        assertEquals(detail, result.getDetail());
    }

    @Test
    void builder_whenAllFieldsProvided_shouldCreateCompleteResponse() {

        // Arrange
        URI type = URI.create("/problems/internal-error");
        URI instance = URI.create("/users/1");
        Instant timestamp = Instant.now();

        ValidationError validationError =
                ValidationError.of("cpf", "Invalid CPF");

        // Act
        ErrorResponse result = ErrorResponse.builder()
                .type(type)
                .title("Validation Error")
                .status(422)
                .detail("Invalid input")
                .instance(instance)
                .timestamp(timestamp)
                .correlationId("123")
                .errors(List.of(validationError))
                .properties(Map.of("key", "value"))
                .build();

        // Assert
        assertEquals(type, result.getType());
        assertEquals(instance, result.getInstance());
        assertEquals(timestamp, result.getTimestamp());
        assertEquals("123", result.getCorrelationId());

        assertNotNull(result.getErrors());
        assertNotNull(result.getProperties());

        assertEquals(1, result.getErrors().size());
        assertEquals("value", result.getProperties().get("key"));
    }
}