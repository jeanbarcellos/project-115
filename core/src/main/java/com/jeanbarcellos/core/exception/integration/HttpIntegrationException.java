package com.jeanbarcellos.core.exception.integration;

import java.util.Map;

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
            Throwable cause) {
        super(service, status, message, responseBody, metadata, cause);
        this.method = method;
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }
}