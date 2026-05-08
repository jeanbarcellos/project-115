package com.jeanbarcellos.core.exception.integration;

import java.util.Map;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.integration.ExternalErrorType;

public class ExternalSystemException extends IntegrationException {

    public ExternalSystemException(
            String service,
            String message,
            Map<String, Object> metadata,
            ExternalErrorType externalError,
            Throwable cause) {
        super(service, null, message, message, metadata, externalError, TechnicalErrorType.EXTERNAL_SERVICE_ERROR,
                cause);
    }
}