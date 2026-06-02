package com.jeanbarcellos.core.exception.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Testes de unidade da classe {@link StorageIntegrationException}.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class StorageIntegrationExceptionTest {

    @Test
    void constructor_whenAllArgumentsProvided_shouldCreateStorageException() {

        // Arrange
        String resource = "documents/file.pdf";

        // Act
        StorageIntegrationException result =
                new StorageIntegrationException(
                        "s3-documents",
                        resource,
                        "Upload failed",
                        Map.of(),
                        TestExternalErrorType.GENERIC,
                        null);

        // Assert
        assertEquals("s3-documents", result.getService());
        assertEquals(resource, result.getResource());
    }
}