package com.jeanbarcellos.architecture.governance.external.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.rule.ValidationRule;
import com.jeanbarcellos.core.error.ExternalErrorType;

/**
 * Garante que o status retornado pelo provider
 * externo seja válido.
 *
 * <p>
 * São aceitos valores entre 100 e 599.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ExternalErrorStatusRule
        implements ValidationRule<ExternalErrorType> {

    /**
     * Executa a validação.
     *
     * @param target  erro externo validado
     * @param context contexto compartilhado
     */
    @Override
    public void validate(ExternalErrorType target, ValidationContext context) {

        Integer status = target.getStatus();

        if (status == null) {
            context.addViolation(
                    ValidationCategory.EXTERNAL_ERROR,
                    target.getClass(),
                    "External error status cannot be null: " + target.getCode());
            return;
        }

        if (status < 100 || status > 599) {
            context.addViolation(
                    ValidationCategory.EXTERNAL_ERROR,
                    target.getClass(),
                    "Invalid external error status: " + target.getCode() + " -> " + status);
        }
    }

}