package com.jeanbarcellos.core.apierror;

import java.net.URI;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true) // retira o prefixo getsset
public enum TechnicalErrorType implements ApiErrorType {

    INTERNAL_ERROR(
            "internal-error",
            500,
            "Internal server error"),

    DATABASE_UNAVAILABLE(
            "database-unavailable",
            503,
            "Database unavailable");

    private final URI type;
    private final int httpStatus;
    private final String title;

    TechnicalErrorType(String code, int httpStatus, String title) {
        this.type = URI.create("https://api.exemplo.com/problems/v1/" + code);
        this.httpStatus = httpStatus;
        this.title = title;
    }

}