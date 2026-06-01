package com.jeanbarcellos.architecture.validation.external.rule;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.report.ValidationCategory;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;
import com.jeanbarcellos.core.error.ExternalErrorType;

/**
 * Garante que a descrição do erro externo
 * esteja preenchida.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ExternalErrorDescriptionRule
        implements ValidationRule<ExternalErrorType> {

    /**
     * Executa a validação.
     *
     * @param target  erro externo validado
     * @param context contexto compartilhado
     */
    @Override
    public void validate(ExternalErrorType target, ValidationContext context) {

        String description = target.getDescription();

        if (description == null || description.isBlank()) {

            context.getReport().addViolation(
                    ValidationCategory.EXTERNAL_ERROR,
                    "External error description cannot be empty: " + target.getCode());
        }
    }

}