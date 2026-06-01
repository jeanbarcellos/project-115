package com.jeanbarcellos.architecture.validation.error.rule;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.report.ValidationCategory;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;
import com.jeanbarcellos.core.error.ErrorType;

/**
 * Garante que o título do erro esteja preenchido.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorTitleRule
        implements ValidationRule<ErrorType> {

    /**
     * Executa a validação.
     *
     * @param target  erro validado
     * @param context contexto compartilhado
     */
    @Override
    public void validate(
            ErrorType target,
            ValidationContext context) {

        if (target.getTitle() == null
                || target.getTitle().isBlank()) {

            context.getReport().addViolation(
                    ValidationCategory.ERROR_TYPE,
                    "Error title cannot be empty: " + target.getCode());
        }
    }

}