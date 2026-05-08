package com.jeanbarcellos.core.exception.integration;

import java.util.Map;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.exception.ApplicationException;
import com.jeanbarcellos.core.integration.ExternalErrorType;

import lombok.Getter;

/**
 * Exceção para falhas em integrações externas (REST, fila, etc).
 */
@Getter
public class IntegrationException extends ApplicationException {

    private final String service; // nome do serviço externo
    private final Integer status; // HTTP status (se aplicável)
    private final String errorBody; // resposta retornada (opcional)

    private final Map<String, Object> metadata;

    private final TechnicalErrorType errorType; // interno
    private final ExternalErrorType externalError; // externo

    public IntegrationException(
            String service,
            Integer status,
            String message,
            String errorBody,
            Map<String, Object> metadata,
            ExternalErrorType externalError,
            TechnicalErrorType errorType,
            Throwable cause)
            {

        super(message, cause);
        this.service = service;
        this.status = status;
        this.errorBody = errorBody;
        this.metadata = metadata;
        this.externalError = externalError;
        this.errorType = errorType;
    }

}