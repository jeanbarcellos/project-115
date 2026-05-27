package com.jeanbarcellos.project115.endereco;

import com.jeanbarcellos.core.integration.ExternalErrorType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SerproErrorType implements ExternalErrorType {

    NOT_FOUND("404", "Resource not found", false),
    INVALID_REQUEST("400", "Invalid request", false),
    INTERNAL_ERROR("500", "Internal error", true),
    TIMEOUT("timeout", "Timeout calling Serpro", true);

    private final String code;

    private final String description;

    private final boolean retryable;

}