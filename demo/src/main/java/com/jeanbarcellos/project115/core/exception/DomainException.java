package com.jeanbarcellos.project115.core.exception;

import com.jeanbarcellos.project115.core.error.BusinessErrorType;

public class DomainException extends RuntimeException {

    private final BusinessErrorType errorType;

    public DomainException(BusinessErrorType errorType, String detail) {
        super(detail);
        this.errorType = errorType;
    }

    public BusinessErrorType getErrorType() {
        return errorType;
    }
}