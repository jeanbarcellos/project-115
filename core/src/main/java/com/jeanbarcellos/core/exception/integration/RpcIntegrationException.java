package com.jeanbarcellos.core.exception.integration;

import java.util.Map;

import com.jeanbarcellos.core.error.ExternalErrorType;

import lombok.Getter;

/**
 * Exceção para falhas em integrações RPC
 * (gRPC, Thrift, Dubbo, etc).
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@SuppressWarnings({ "java:S110", "java:S1948" })
public class RpcIntegrationException extends IntegrationException {

    /**
     * Método RPC executado.
     */
    private final String method;

    /**
     * Cria uma nova exceção RPC.
     *
     * @param service       identificador lógico da integração
     * @param method        método RPC executado
     * @param message       mensagem resumida da falha
     * @param metadata      metadados auxiliares
     * @param externalError erro retornado pelo provider
     * @param cause         causa raiz da falha
     */
    public RpcIntegrationException(
            String service,
            String method,
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
    }

}