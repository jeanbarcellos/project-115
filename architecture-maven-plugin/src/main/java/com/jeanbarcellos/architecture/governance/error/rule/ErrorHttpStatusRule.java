package com.jeanbarcellos.architecture.governance.error.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.rule.ValidationRule;
import com.jeanbarcellos.core.error.ErrorType;

/**
 * Garante que os status HTTP dos erros
 * estejam dentro da faixa válida para erros.
 *
 * <p>
 * Atualmente são aceitos apenas status
 * entre 400 e 599.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorHttpStatusRule
        implements ValidationRule<ErrorType> {

    /**
     * Executa a validação.
     *
     * @param target  erro validado
     * @param context contexto compartilhado
     */
    @Override
    public void validate(ErrorType target, ValidationContext context) {

        int status = target.getHttpStatus();

        if (status < 400 || status > 599) {

            context.addViolation(
                    ValidationCategory.ERROR_TYPE,
                    target.getClass(),
                    "Invalid HTTP status for error: " + target.getCode());
        }
    }

}