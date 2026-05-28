package com.jeanbarcellos.core.exception.integration;

import java.util.Map;

import com.jeanbarcellos.core.integration.ExternalErrorType;

import lombok.Getter;

/**
 * Exceção para falhas em integrações de storage externo
 * (S3, MinIO, Redis, etc).
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@SuppressWarnings({ "java:S110", "java:S1948" })
public class StorageIntegrationException extends IntegrationException {

    /**
     * Recurso acessado durante a operação.
     *
     * <p>
     * Exemplos:
     * bucket, objeto, chave, arquivo.
     * </p>
     */
    private final String resource;

    /**
     * Cria uma nova exceção de storage.
     *
     * @param service       identificador lógico da integração
     * @param resource      recurso acessado
     * @param message       mensagem resumida da falha
     * @param metadata      metadados auxiliares
     * @param externalError erro retornado pelo provider
     * @param cause         causa raiz da falha
     */
    public StorageIntegrationException(
            String service,
            String resource,
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

        this.resource = resource;
    }
}