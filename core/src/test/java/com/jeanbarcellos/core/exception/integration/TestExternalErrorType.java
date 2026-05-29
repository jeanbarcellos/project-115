package com.jeanbarcellos.core.exception.integration;

import com.jeanbarcellos.core.error.ErrorType;
import com.jeanbarcellos.core.error.ExternalErrorType;
import com.jeanbarcellos.core.error.TechnicalErrorType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Implementação auxiliar utilizada nos testes.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@RequiredArgsConstructor
enum TestExternalErrorType implements ExternalErrorType {

    GENERIC(
            500,
            "GENERIC",
            "Generic provider error",
            true,
            TechnicalErrorType.EXTERNAL_SERVICE_ERROR);

    private final Integer status;
    private final String code;
    private final String description;
    private final boolean retryable;
    private final ErrorType errorType;

}