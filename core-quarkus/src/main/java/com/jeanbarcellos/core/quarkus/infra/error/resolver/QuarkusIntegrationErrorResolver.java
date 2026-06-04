package com.jeanbarcellos.core.quarkus.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public class QuarkusIntegrationErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        if (hasCause(ex, "java.net.SocketTimeoutException")
                || hasCause(ex, "java.net.http.HttpTimeoutException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_TIMEOUT;
        }

        if (hasCause(ex, "java.net.SocketTimeoutException")
                || hasCause(ex, "java.net.http.HttpTimeoutException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_TIMEOUT;
        }

        if (hasCause(ex, "org.eclipse.microprofile.rest.client.RestClientException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }

        if (hasCause(ex, "org.jboss.resteasy.reactive.ClientWebApplicationException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }

        if (hasCause(ex, "feign.codec.DecodeException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_BAD_RESPONSE;
        }

        if (hasCause(ex, "com.fasterxml.jackson.databind.JsonMappingException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_BAD_RESPONSE;
        }

        // Caso o projeto utilize Feign Client ao invés do MicroProfile Rest Client
        if (isInstanceOf(ex.getClass(), "feign.FeignException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }

        if (isInstanceOf(ex.getClass(), "feign.RetryableException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_UNAVAILABLE;
        }

        return null;
    }

}
