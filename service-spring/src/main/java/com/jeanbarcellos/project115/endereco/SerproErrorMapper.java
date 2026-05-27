package com.jeanbarcellos.project115.endereco;

import com.jeanbarcellos.core.error.TechnicalErrorType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SerproErrorMapper {

    public static TechnicalErrorType map(int status) {

        if (status == 404) {
            return TechnicalErrorType.RESOURCE_NOT_FOUND;
        }

        if (status >= 400) {
            return TechnicalErrorType.DEPENDENCY_FAILURE;
        }

        if (status >= 500) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }

        return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
    }

    public static TechnicalErrorType map(SerproErrorType external) {

        // regra simples: retryable → erro técnico retriável
        if (external.isRetryable()) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }

        // regras específicas
        return switch (external) {
            case NOT_FOUND       -> TechnicalErrorType.RESOURCE_NOT_FOUND;
            case INVALID_REQUEST -> TechnicalErrorType.DEPENDENCY_FAILURE;
            default              -> TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        };
    }
}