package com.jeanbarcellos.core.exception;

import java.util.Map;

import com.jeanbarcellos.core.error.ApiErrorType;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

    private final ApiErrorType type;
    private final Map<String, Object> context;

    public DomainException(ApiErrorType type, String detail) {
        this(type, detail, Map.of());
    }

    public DomainException(ApiErrorType type, String detail, Map<String, Object> context) {
        super(detail);
        this.type = type;
        this.context = context;
    }

}