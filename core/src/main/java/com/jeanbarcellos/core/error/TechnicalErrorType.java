package com.jeanbarcellos.core.error;

import java.net.URI;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true) // retira o prefixo getsset
public enum TechnicalErrorType implements ApiErrorType {

    INTERNAL_ERROR("internal-error", 500, "Internal server error"),
    DATABASE_ERROR("database-error", 503, "Database unavailable"),
    ALIDATION_ERROR("validation-error", 422, "Validation failed"),
    MALFORMED_JSON("malformed-json", 400, "Malformed JSON"),
    INVALID_PARAMETER("invalid-parameter", 400, "Invalid parameter"),
    UNEXPECTED_ERROR("unexpected-error", 500, "Unexpected error");

    private final String code;
    private final URI type;
    private final int httpStatus;
    private final String title;

    TechnicalErrorType(String code, int httpStatus, String title) {
        this.code = code;
        this.type = URI.create("https://api.exemplo.com/problems" + code);
        this.httpStatus = httpStatus;
        this.title = title;
    }

}