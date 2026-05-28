package com.jeanbarcellos.project115.infra.error;

import com.jeanbarcellos.core.error.ErrorAttributes;
import com.jeanbarcellos.core.error.ErrorAttributesResolver;
import com.jeanbarcellos.core.error.ErrorCategory;
import com.jeanbarcellos.core.error.ErrorLogLevel;
import com.jeanbarcellos.core.error.ErrorSeverity;
import com.jeanbarcellos.core.error.ErrorType;
import com.jeanbarcellos.core.error.TechnicalErrorType;

import lombok.RequiredArgsConstructor;

/**
 * Implementação padrão para resolução
 * de atributos operacionais de erros.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@RequiredArgsConstructor
public class DefaultErrorAttributesResolver
        implements ErrorAttributesResolver {

    @Override
    public ErrorAttributes resolve(
            ErrorCategory category,
            ErrorType errorType,
            Exception exception) {

        // integrações
        if (category == ErrorCategory.INTEGRATION) {

            // timeout/retry
            if (errorType.isRetryable()) {
                return ErrorAttributes.of(ErrorSeverity.HIGH, ErrorLogLevel.ERROR, true);
            }

            // 404 funcional
            if (errorType == TechnicalErrorType.RESOURCE_NOT_FOUND) {
                return ErrorAttributes.of(ErrorSeverity.LOW, ErrorLogLevel.INFO, false);
            }

            return ErrorAttributes.of(ErrorSeverity.MEDIUM, ErrorLogLevel.WARN, false);
        }

        // validação
        if (category == ErrorCategory.VALIDATION) {
            return ErrorAttributes.of(ErrorSeverity.LOW, ErrorLogLevel.WARN, false);
        }

        // negócio
        if (category == ErrorCategory.BUSINESS) {

            return ErrorAttributes.of(ErrorSeverity.LOW, ErrorLogLevel.INFO, false);
        }

        // técnico
        return ErrorAttributes.of(ErrorSeverity.HIGH, ErrorLogLevel.ERROR, true);
    }
}