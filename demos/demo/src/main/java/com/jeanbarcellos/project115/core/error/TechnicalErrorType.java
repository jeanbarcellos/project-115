package com.jeanbarcellos.project115.core.error;

import java.net.URI;

import org.springframework.http.HttpStatus;

public enum TechnicalErrorType implements ApiErrorType {

    INTERNAL_ERROR(
        "internal-error",
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Internal server error"
    ),

    DATABASE_UNAVAILABLE(
        "database-unavailable",
        HttpStatus.SERVICE_UNAVAILABLE,
        "Database unavailable"
    );

    private final URI type;
    private final HttpStatus status;
    private final String title;

    TechnicalErrorType(String code, HttpStatus status, String title) {
        this.type = URI.create("https://api.exemplo.com/problems/v1/" + code);
        this.status = status;
        this.title = title;
    }

    public URI type() { return type; }
    public HttpStatus status() { return status; }
    public String title() { return title; }
}