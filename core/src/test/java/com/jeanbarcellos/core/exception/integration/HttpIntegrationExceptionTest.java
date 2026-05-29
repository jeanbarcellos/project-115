package com.jeanbarcellos.core.exception.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Testes unitários da classe {@link HttpIntegrationException}.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class HttpIntegrationExceptionTest {

    @Test
    void constructor_whenAllArgumentsProvided_shouldCreateHttpException() {

        // Arrange
        URI uri = URI.create("https://api.serpro.gov.br/customers");

        // Act
        HttpIntegrationException result = new HttpIntegrationException(
                "rest-serpro",
                "GET",
                uri,
                404,
                "Not found",
                "{}",
                Map.of(),
                TestExternalErrorType.GENERIC,
                null);

        // Assert
        assertEquals("GET", result.getMethod());
        assertEquals(uri, result.getUri());
        assertEquals("rest-serpro", result.getService());
    }
}