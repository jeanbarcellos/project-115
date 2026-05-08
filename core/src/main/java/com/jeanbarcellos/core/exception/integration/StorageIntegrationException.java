package com.jeanbarcellos.core.exception.integration;

import java.util.Map;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.integration.ExternalErrorType;

import lombok.Getter;

/**
 * Erros em storage externo (Redis, S3, etc).
 */
@Getter
public class StorageIntegrationException extends IntegrationException {

    private final String resource;

    public StorageIntegrationException(
            String service,
            String resource,
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
                TechnicalErrorType.CONNECTION_ERROR,
                cause);
        this.resource = resource;
    }

}