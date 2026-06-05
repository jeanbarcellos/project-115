package com.jeanbarcellos.core.spring.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public class SpringIntegrationErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        // OpenFeign Clients

        if (hasCause(ex, "feign.codec.DecodeException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_BAD_RESPONSE;
        }

        if (hasCause(ex, "com.fasterxml.jackson.databind.JsonMappingException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_BAD_RESPONSE;
        }

        if (hasCause(ex, "org.springframework.core.codec.DecodingException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_BAD_RESPONSE;
        }

        if (hasCause(ex, "feign.RetryableException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_UNAVAILABLE;
        }

        if (hasCause(ex, "feign.FeignException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }

        // RestTemplate

        if (hasCause(ex, "org.springframework.web.client.UnknownHttpStatusCodeException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_BAD_RESPONSE;
        }

        if (hasCause(ex, "org.springframework.web.client.HttpStatusCodeException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }
        if (hasCause(ex, "org.springframework.web.client.ResourceAccessException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }

        // WebClient

        if (hasCause(ex,
                "org.springframework.web.reactive.function.client.WebClientResponseException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }

        if (hasCause(ex,
                "org.springframework.web.reactive.function.client.WebClientRequestException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }

        // Apache HttpClient

        if (isInstanceOf(ex.getClass(),
                "org.apache.hc.client5.http.ConnectTimeoutException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_TIMEOUT;
        }

        return null;
    }

}
