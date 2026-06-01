package com.jeanbarcellos.architecture.governance.error.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.rule.ValidationRule;
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