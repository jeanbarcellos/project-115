package com.jeanbarcellos.core.quarkus.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public class QuarkusResourceErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        if (ex instanceof jakarta.ws.rs.NotFoundException) {
            return TechnicalErrorType.RESOURCE_NOT_FOUND;
        }

        if (ex instanceof jakarta.ws.rs.NotAllowedException) {
            return TechnicalErrorType.RESOURCE_METHOD_NOT_ALLOWED;
        }

        if (ex instanceof jakarta.ws.rs.NotSupportedException) {
            return TechnicalErrorType.RESOURCE_UNSUPPORTED_MEDIA_TYPE;
        }

        if (isInstanceOf(ex.getClass(), "jakarta.persistence.EntityNotFoundException")) {
            return TechnicalErrorType.RESOURCE_NOT_FOUND;
        }

        if (ex instanceof com.fasterxml.jackson.core.JsonParseException) {
            return TechnicalErrorType.MALFORMED_JSON;
        }

        return null;
    }

}