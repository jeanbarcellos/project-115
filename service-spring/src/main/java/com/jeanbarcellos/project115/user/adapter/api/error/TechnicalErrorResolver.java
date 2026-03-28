package com.jeanbarcellos.project115.user.adapter.api.error;

import com.jeanbarcellos.core.error.TechnicalErrorType;

public class TechnicalErrorResolver {

    public static TechnicalErrorType resolve(Throwable ex) {

        if (ex instanceof java.sql.SQLException) {
            return TechnicalErrorType.DATABASE_ERROR;
        }

        if (ex instanceof com.fasterxml.jackson.core.JsonParseException) {
            return TechnicalErrorType.MALFORMED_JSON;
        }

        if (ex instanceof org.springframework.http.converter.HttpMessageNotReadableException) {
            return TechnicalErrorType.MALFORMED_JSON;
        }

        if (ex instanceof org.springframework.web.method.annotation.MethodArgumentTypeMismatchException) {
            return TechnicalErrorType.INVALID_PARAMETER;
        }

        return TechnicalErrorType.INTERNAL_ERROR;
    }
}