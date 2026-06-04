package com.jeanbarcellos.core.spring.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public class SpringFallbackErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        // Captura exceções genéricas lançadas manualmente como throw new
        // ResponseStatusException(...)
        if (ex instanceof org.springframework.web.server.ResponseStatusException responseStatusEx) {

            int status = responseStatusEx.getStatusCode().value();

            if (status == 400)
                return TechnicalErrorType.INVALID_PARAMETER;
            if (status == 401)
                return TechnicalErrorType.UNAUTHORIZED;
            if (status == 403)
                return TechnicalErrorType.FORBIDDEN;
            if (status == 404)
                return TechnicalErrorType.RESOURCE_NOT_FOUND;
            if (status == 405)
                return TechnicalErrorType.RESOURCE_METHOD_NOT_ALLOWED;
            if (status == 409)
                return TechnicalErrorType.CONFLICT;
            if (status == 415)
                return TechnicalErrorType.RESOURCE_UNSUPPORTED_MEDIA_TYPE;
            if (status == 429)
                return TechnicalErrorType.RATE_LIMIT_EXCEEDED;
        }

        return null;
    }

}
