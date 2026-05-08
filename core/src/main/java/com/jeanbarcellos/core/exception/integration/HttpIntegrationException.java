package com.jeanbarcellos.core.exception.integration;

import java.util.Map;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.integration.ExternalErrorType;

import lombok.Getter;

/**
 * Erros de integração HTTP (REST).
 */
@Getter
public class HttpIntegrationException extends IntegrationException {

    private final String method;
    private final String url;

    public HttpIntegrationException(
            String service,
            String method,
            String url,
            Integer status,
            String message,
            String responseBody,
            Map<String, Object> metadata,
            ExternalErrorType externalError,
            TechnicalErrorType errorType,
            Throwable cause) {
        super(service, status, message, responseBody, metadata, externalError, resolveErrorType(status), cause);
        this.method = method;
        this.url = url;
    }

    private static TechnicalErrorType resolveErrorType(Integer status) {

        if (status == null) {
            return TechnicalErrorType.EXTERNAL_SERVICE_TIMEOUT;
        }

        if (status >= 500) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }

        if (status >= 400) {
            return TechnicalErrorType.DEPENDENCY_FAILURE;
        }

        return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
    }

}