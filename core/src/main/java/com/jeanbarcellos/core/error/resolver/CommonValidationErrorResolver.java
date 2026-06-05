package com.jeanbarcellos.core.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;

/**
 * Resolver responsável por erros de validação independentes
 * de framework.
 *
 * @author Jean Barcellos
 */
public class CommonValidationErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        if (ex instanceof jakarta.validation.ConstraintViolationException) {
            return TechnicalErrorType.INPUT_VALIDATION_ERROR;
        }

        if (ex instanceof jakarta.validation.ValidationException) {
            return TechnicalErrorType.SYSTEM_VALIDATION_ERROR;
        }

        return null;
    }

}