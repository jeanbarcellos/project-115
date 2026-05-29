package com.jeanbarcellos.core.exception.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Testes unitários da classe {@link MessagingIntegrationException}.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class MessagingIntegrationExceptionTest {

    @Test
    void constructor_whenAllArgumentsProvided_shouldCreateMessagingException() {

        // Arrange
        String topic = "wallet.created";
        String operation = "publish";

        // Act
        MessagingIntegrationException result = new MessagingIntegrationException(
                "kafka-wallet",
                topic,
                operation,
                "Publish failed",
                Map.of(),
                TestExternalErrorType.GENERIC,
                null);

        // Assert
        assertEquals("kafka-wallet", result.getService());
        assertEquals(topic, result.getTopicOrQueue());
        assertEquals(operation, result.getOperation());
    }
}