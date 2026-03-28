package com.jeanbarcellos.core.exception;

import java.util.Map;

import com.jeanbarcellos.core.error.ApiErrorType;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

    private final ApiErrorType errorType;
    private final Map<String, Object> context;

    public DomainException(ApiErrorType errorType, String detail) {
        super(detail);
        this.errorType = errorType;
        this.context = null;
    }

    public DomainException(ApiErrorType errorType, String detail, Map<String, Object> context) {
        super(detail);
        this.errorType = errorType;
        this.context = context;
    }

}