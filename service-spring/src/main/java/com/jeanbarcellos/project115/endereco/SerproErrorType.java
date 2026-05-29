package com.jeanbarcellos.project115.endereco;

import com.jeanbarcellos.core.error.ErrorType;
import com.jeanbarcellos.core.error.ExternalErrorType;
import com.jeanbarcellos.core.error.TechnicalErrorType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SerproErrorType implements ExternalErrorType {

    NOT_FOUND(
            404,
            "NOT_FOUND",
            "Resource not found",
            false,
            TechnicalErrorType.RESOURCE_NOT_FOUND),

    INVALID_REQUEST(
            400,
            "INVALID_REQUEST",
            "Invalid request",
            false,
            TechnicalErrorType.DEPENDENCY_FAILURE),

    INTERNAL_ERROR(
            500,
            "INTERNAL_ERROR",
            "Internal error",
            true,
            TechnicalErrorType.EXTERNAL_SERVICE_ERROR),

    TIMEOUT(
            504,
            "timeout",
            "Timeout calling Serpro",
            true,
            TechnicalErrorType.EXTERNAL_SERVICE_TIMEOUT);


    private final Integer status;

    private final String code;

    private final String description;

    private final boolean retryable;

    private final ErrorType errorType;

}