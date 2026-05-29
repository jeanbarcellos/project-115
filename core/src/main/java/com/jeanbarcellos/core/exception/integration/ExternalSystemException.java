package com.jeanbarcellos.core.exception.integration;

import java.util.Map;

import com.jeanbarcellos.core.error.ExternalErrorType;

/**
 * Exceção genérica para falhas em sistemas externos
 * que não possuem uma categoria/protocolo específico.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@SuppressWarnings({ "java:S110", "java:S1948" })
public class ExternalSystemException extends IntegrationException {

    /**
     * Cria uma nova exceção genérica de sistema externo.
     *
     * @param service       identificador lógico da integração
     * @param message       mensagem resumida da falha
     * @param metadata      metadados auxiliares
     * @param externalError erro retornado pelo provider
     * @param cause         causa raiz da falha
     */
    public ExternalSystemException(
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