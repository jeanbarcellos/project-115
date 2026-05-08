package com.jeanbarcellos.core.exception.integration;

import java.util.Map;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.integration.ExternalErrorType;

import lombok.Getter;

/**
 * Erros de chamadas RPC (gRPC, etc).
 */
@Getter
public class RpcIntegrationException extends IntegrationException {

    private final String method;

    public RpcIntegrationException(
            String service,
            String method,
            String message,
            Map<String, Object> metadata,
            ExternalErrorType externalError,
            Throwable cause) {
        super(
                service,
                null,
                message,
                null,
                metadata,
                externalError,
                TechnicalErrorType.EXTERNAL_SERVICE_ERROR,
                cause);
        this.method = method;
    }

}