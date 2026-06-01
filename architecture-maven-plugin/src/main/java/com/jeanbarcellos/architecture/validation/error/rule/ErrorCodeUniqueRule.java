package com.jeanbarcellos.architecture.validation.error.rule;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.report.ValidationCategory;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;
import com.jeanbarcellos.core.error.ErrorType;

/**
 * Garante unicidade global dos códigos
 * de erro da plataforma.
 *
 * <p>
 * Dois erros não podem compartilhar o
 * mesmo código, independentemente do módulo
 * em que foram declarados.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorCodeUniqueRule
        implements ValidationRule<ErrorType> {

    /**
     * Executa a validação.
     *
     * @param target  erro validado
     * @param context contexto compartilhado
     */
    @Override
    public void validate(ErrorType target, ValidationContext context) {

        if (!context.getErrorCodes().add(target.getCode())) {
            context.getReport().addViolation(
                    ValidationCategory.ERROR_TYPE,
                    "Duplicated error code detected: " + target.getCode());
        }
    }

}