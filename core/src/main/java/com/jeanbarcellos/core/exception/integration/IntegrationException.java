package com.jeanbarcellos.core.exception.integration;

import java.util.Map;

import com.jeanbarcellos.core.exception.ApplicationException;

/**
 * Exceção para falhas em integrações externas (REST, fila, etc).
 */
public class IntegrationException extends ApplicationException {

    private final String service; // nome do serviço externo
    private final Integer status; // HTTP status (se aplicável)
    private final String errorBody; // resposta retornada (opcional)
    private final Map<String, Object> metadata;

    public IntegrationException(
            String service,
            Integer status,
            String message,
            String errorBody,
            Map<String, Object> metadata,
            Throwable cause) {
        super(message, cause);
        this.service = service;
        this.status = status;
        this.errorBody = errorBody;
        this.metadata = metadata;
    }

    public String getService() {
        return service;
    }

    public Integer getStatus() {
        return status;
    }

    public String getErrorBody() {
        return errorBody;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}