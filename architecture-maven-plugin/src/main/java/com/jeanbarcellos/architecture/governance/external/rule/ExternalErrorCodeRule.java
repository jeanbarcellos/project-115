package com.jeanbarcellos.architecture.governance.external.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.rule.ValidationRule;
import com.jeanbarcellos.core.error.ExternalErrorType;

/**
 * Garante que códigos externos estejam preenchidos.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ExternalErrorCodeRule
        implements ValidationRule<ExternalErrorType> {

    /**
     * Executa a validação.
     *
     * @param target  erro externo validado
     * @param context contexto compartilhado
     */
    @Override
    public void validate(ExternalErrorType target, ValidationContext context) {

        String code = target.getCode();

        if (code == null || code.isBlank()) {
            context.addViolation(
                    ValidationCategory.EXTERNAL_ERROR,
                    target.getClass(),
                    "External error code cannot be empty.");
        }
    }

}