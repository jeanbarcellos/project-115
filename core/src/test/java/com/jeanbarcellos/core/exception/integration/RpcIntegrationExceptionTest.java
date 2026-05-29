package com.jeanbarcellos.core.exception.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Testes unitários da classe {@link RpcIntegrationException}.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class RpcIntegrationExceptionTest {

    @Test
    void constructor_whenAllArgumentsProvided_shouldCreateRpcException() {

        // Arrange
        String method = "LedgerService/CreateTransaction";

        // Act
        RpcIntegrationException result = new RpcIntegrationException(
                "grpc-ledger",
                method,
                "Ledger unavailable",
                Map.of(),
                TestExternalErrorType.GENERIC,
                null);

        // Assert
        assertEquals("grpc-ledger", result.getService());
        assertEquals(method, result.getMethod());
    }
}