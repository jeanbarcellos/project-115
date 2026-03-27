package com.jeanbarcellos.project115.core.error;

import java.net.URI;

import org.springframework.http.HttpStatus;

public enum BusinessErrorType implements ApiErrorType {

    USER_NOT_FOUND(
            "user-not-found",
            HttpStatus.NOT_FOUND,
            "User not found"),

    INSUFFICIENT_BALANCE(
            "insufficient-balance",
            HttpStatus.CONFLICT,
            "Insufficient balance");

    private final URI type;
    private final HttpStatus status;
    private final String title;

    BusinessErrorType(String code, HttpStatus status, String title) {
        this.type = URI.create("https://api.exemplo.com/problems/v1/" + code);
        this.status = status;
        this.title = title;
    }

    public URI type() {
        return type;
    }

    public HttpStatus status() {
        return status;
    }

    public String title() {
        return title;
    }
}