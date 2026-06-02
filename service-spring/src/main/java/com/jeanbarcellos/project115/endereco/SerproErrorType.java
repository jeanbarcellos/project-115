package com.jeanbarcellos.project115.endereco;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            "NOT-FOUND",
            "Resource not found",
            false,
            TechnicalErrorType.RESOURCE_NOT_FOUND),

    INVALID_REQUEST(
            400,
            "INVALID-REQUEST",
            "Invalid request",
            false,
            TechnicalErrorType.DEPENDENCY_FAILURE),

    INTERNAL_ERROR(
            500,
            "INTERNAL-ERROR",
            "Internal error",
            true,
            TechnicalErrorType.EXTERNAL_SERVICE_ERROR),

    TIMEOUT(
            504,
            "timeout",
            "Timeout calling Serpro",
            true,
            TechnicalErrorType.EXTERNAL_SERVICE_TIMEOUT);

    private static final Map<Integer, SerproErrorType> MAPA_ERROS = Arrays.stream(values())
            .collect(Collectors.toMap(
                    SerproErrorType::getStatus,
                    Function.identity(),
                    (existente, substituto) -> existente));
    private final Integer status;
    private final String code;
    private final String description;
    private final boolean retryable;
    private final ErrorType errorType;

    public static SerproErrorType fromStatus(Integer status) {
        return MAPA_ERROS.get(status);
    }

    public static String getDescriptionByStatus(Integer status) {
        SerproErrorType erro = fromStatus(status);
        return erro != null ? erro.getDescription() : "Erro desconhecido (" + status + ")";
    }

    public static boolean isValidStatus(Integer status) {
        return MAPA_ERROS.containsKey(status);
    }

}