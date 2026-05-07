package com.jeanbarcellos.core.exception.integration;

import java.util.Map;

public class RpcIntegrationException extends IntegrationException {

    private final String method;

    public RpcIntegrationException(
            String service,
            String method,
            String message,
            Map<String, Object> metadata,
            Throwable cause
    ) {
        super(service, null, message, null, metadata, cause);
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}