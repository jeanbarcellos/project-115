package com.jeanbarcellos.core.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Enumeração de erros técnicos comuns da aplicação.
 *
 * <p>
 * Esses erros são independentes de domínio e podem ser reutilizados
 * por qualquer módulo da aplicação.
 * </p>
 */
@Getter
@RequiredArgsConstructor
@Accessors(fluent = true) // retira o prefixo getsset
public enum TechnicalErrorType implements ApiErrorType {

    INTERNAL_ERROR("internal-error", 500, "Internal server error", false),
    DATABASE_ERROR("database-error", 503, "Database unavailable", true),
    TIMEOUT("timeout", 504, "Timeout", true),
    MALFORMED_JSON("malformed-json", 400, "Malformed JSON", false),
    INVALID_PARAMETER("invalid-parameter", 400, "Invalid parameter", false),
    VALIDATION_ERROR("validation-error", 422, "Validation failed", false);

    private final String code;
    private final int httpStatus;
    private final String title;
    private final boolean retryable;

    /**
     * Indica se o erro pode ser reprocessado.
     *
     * @return true se retry é recomendado
     */
    public boolean isRetryable() {
        return retryable;
    }

}