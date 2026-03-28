package com.jeanbarcellos.core.apierror;

public class DomainException extends RuntimeException {

    private final ApiErrorType errorType;

    public DomainException(ApiErrorType errorType, String detail) {
        super(detail);
        this.errorType = errorType;
    }

    public ApiErrorType getErrorType() {
        return errorType;
    }
}