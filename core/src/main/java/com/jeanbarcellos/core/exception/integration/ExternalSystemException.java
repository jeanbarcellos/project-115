package com.jeanbarcellos.core.exception.integration;

import java.util.Map;

public class ExternalSystemException extends IntegrationException {

    public ExternalSystemException(
            String service,
            String message,
            Map<String, Object> metadata,
            Throwable cause
    ) {
        super(service, null, message, null, metadata, cause);
    }
}