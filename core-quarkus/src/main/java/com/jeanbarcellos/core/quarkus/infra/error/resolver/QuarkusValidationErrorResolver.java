package com.jeanbarcellos.core.quarkus.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public class QuarkusValidationErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        // Erro de Bean Validation (Ex: @NotNull, @Email no corpo ou parâmetros)
        if (ex instanceof jakarta.validation.ConstraintViolationException) {
            return TechnicalErrorType.INPUT_VALIDATION_ERROR;
        }

        if (ex instanceof jakarta.validation.ValidationException) {
            return TechnicalErrorType.SYSTEM_VALIDATION_ERROR;
        }
        if (ex instanceof jakarta.ws.rs.BadRequestException) {
            return TechnicalErrorType.INVALID_PARAMETER;
        }

        if (hasCause(ex,
                "org.jboss.resteasy.reactive.server.validation.ResteasyReactiveViolationException")) {
            return TechnicalErrorType.INPUT_VALIDATION_ERROR;
        }

        return null;
    }

}