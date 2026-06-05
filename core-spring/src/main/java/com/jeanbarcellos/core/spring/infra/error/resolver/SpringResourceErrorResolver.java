package com.jeanbarcellos.core.spring.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public final class SpringResourceErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        if (ex instanceof org.springframework.web.HttpRequestMethodNotSupportedException) {
            return TechnicalErrorType.RESOURCE_METHOD_NOT_ALLOWED;
        }

        if (ex instanceof org.springframework.web.HttpMediaTypeNotSupportedException) {
            return TechnicalErrorType.RESOURCE_UNSUPPORTED_MEDIA_TYPE;
        }

        if (ex instanceof org.springframework.web.HttpMediaTypeNotAcceptableException) {
            return TechnicalErrorType.INVALID_FORMAT;
        }

        if (isInstanceOf(ex.getClass(), "org.springframework.web.servlet.NoHandlerFoundException")) {
            return TechnicalErrorType.RESOURCE_NOT_FOUND;
        }

        if (ex instanceof org.springframework.dao.EmptyResultDataAccessException) {
            return TechnicalErrorType.RESOURCE_NOT_FOUND;
        }

        if (ex instanceof org.springframework.http.converter.HttpMessageNotReadableException) {
            return TechnicalErrorType.MALFORMED_JSON;
        }

        return null;
    }

}
