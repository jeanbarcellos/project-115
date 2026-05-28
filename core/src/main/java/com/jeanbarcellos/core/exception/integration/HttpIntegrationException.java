package com.jeanbarcellos.core.exception.integration;

import java.net.URI;
import java.util.Map;

import com.jeanbarcellos.core.integration.ExternalErrorType;

import lombok.Getter;

/**
 * Erros de integração HTTP (REST).
 */
@Getter
@SuppressWarnings({ "java:S110", "java:S1948" })
public class HttpIntegrationException extends IntegrationException {

    /**
     * Método HTTP executado.
     *
     * <p>
     * Exemplos:
     * GET, POST, PUT, DELETE.
     * </p>
     */
    private final String method;

    /**
     * URI chamada na integração.
     */
    private final URI uri;

    /**
     * Status retornado pelo provider.
     */
    private final Integer status;

    /**
     * Payload bruto retornado pelo provider.
     *
     * <p>
     * Pode representar:
     * </p>
     *
     * <ul>
     *   <li>JSON;</li>
     *   <li>XML;</li>
     *   <li>texto;</li>
     *   <li>estrutura serializada.</li>
     * </ul>
     */
    private final Object responsePayload;

    /**
     * Cria uma nova exceção de integração HTTP.
     *
     * @param service         identificador lógico da integração
     * @param method          método HTTP executado
     * @param uri             URI chamada
     * @param status          status retornado
     * @param responsePayload payload retornado pelo provider
     * @param message         mensagem resumida da falha
     * @param metadata        metadados auxiliares
     * @param externalError   erro externo mapeado
     * @param cause           causa raiz da falha
     */
    public HttpIntegrationException(
            String service,
            String method,
            URI uri,
            Integer status,
            Object responsePayload,
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

        this.method = method;
        this.uri = uri;
        this.status = status;
        this.responsePayload = responsePayload;
    }

}