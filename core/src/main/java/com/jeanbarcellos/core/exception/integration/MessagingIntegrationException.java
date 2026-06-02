package com.jeanbarcellos.core.exception.integration;

import java.util.Map;

import com.jeanbarcellos.core.error.ExternalErrorType;

import lombok.Getter;

/**
 * Exceção para falhas em integrações de mensageria
 * (Kafka, RabbitMQ, SQS, etc).
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@SuppressWarnings({ "java:S110", "java:S1948" })
public class MessagingIntegrationException extends IntegrationException {

    /**
     * Nome do tópico ou fila utilizado.
     */
    private final String topicOrQueue;

    /**
     * Operação executada.
     *
     * <p>
     * Exemplos:
     * publish, consume, acknowledge.
     * </p>
     */
    private final String operation;

    /**
     * Cria uma nova exceção de mensageria.
     *
     * @param service       identificador lógico da integração
     * @param topicOrQueue  tópico ou fila utilizada
     * @param operation     operação executada
     * @param message       mensagem resumida da falha
     * @param metadata      metadados auxiliares
     * @param externalError erro retornado pelo provider
     * @param cause         causa raiz da falha
     */
    public MessagingIntegrationException(
            String service,
            String topicOrQueue,
            String operation,
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

        this.topicOrQueue = topicOrQueue;
        this.operation = operation;
    }

}