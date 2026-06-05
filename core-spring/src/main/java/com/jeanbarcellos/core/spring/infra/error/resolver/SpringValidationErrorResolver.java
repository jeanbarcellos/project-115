package com.jeanbarcellos.core.spring.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public final class SpringValidationErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        if (ex instanceof org.springframework.web.bind.MethodArgumentNotValidException) {
            return TechnicalErrorType.INPUT_VALIDATION_ERROR;
        }

        if (isInstanceOf(ex.getClass(),
                "org.springframework.validation.method.MethodValidationException")) {
            return TechnicalErrorType.INPUT_VALIDATION_ERROR;
        }

        if (isInstanceOf(ex.getClass(),
                "org.springframework.web.method.annotation.HandlerMethodValidationException")) {
            return TechnicalErrorType.INPUT_VALIDATION_ERROR;
        }

        if (ex instanceof org.springframework.validation.BindException) {
            return TechnicalErrorType.INPUT_VALIDATION_ERROR;
        }

        if (ex instanceof org.springframework.web.method.annotation.MethodArgumentTypeMismatchException) {
            return TechnicalErrorType.INVALID_PARAMETER;
        }

        if (ex instanceof org.springframework.beans.TypeMismatchException) {
            return TechnicalErrorType.INVALID_PARAMETER;
        }

        if (ex instanceof org.springframework.core.convert.ConversionFailedException) {
            return TechnicalErrorType.INVALID_PARAMETER;
        }

        if (ex instanceof org.springframework.beans.ConversionNotSupportedException) {
            return TechnicalErrorType.INVALID_PARAMETER;
        }

        if (ex instanceof org.springframework.web.bind.MissingServletRequestParameterException) {
            return TechnicalErrorType.MISSING_PARAMETER;
        }

        if (ex instanceof org.springframework.web.multipart.support.MissingServletRequestPartException) {
            return TechnicalErrorType.MISSING_PARAMETER;
        }

        if (ex instanceof org.springframework.web.bind.MissingPathVariableException) {
            return TechnicalErrorType.MISSING_PARAMETER;
        }

        if (ex instanceof org.springframework.web.bind.MissingRequestHeaderException) {
            return TechnicalErrorType.MISSING_PARAMETER;
        }

        if (ex instanceof org.springframework.web.bind.MissingRequestCookieException) {
            return TechnicalErrorType.MISSING_PARAMETER;
        }

        if (ex instanceof org.springframework.web.bind.ServletRequestBindingException) {
            return TechnicalErrorType.MISSING_PARAMETER;
        }
        
        return null;
    }

}
