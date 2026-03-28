package com.jeanbarcellos.core.exception;

import java.util.Map;

import com.jeanbarcellos.core.error.ApiErrorType;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ApiErrorType type;
    private final int status;
    private final Map<String, Object> properties;

    public BusinessException(
            ApiErrorType type,
            String detail,
            int status,
            Map<String, Object> properties) {

        super(detail);
        this.type = type;
        this.status = status;
        this.properties = properties;
    }

}