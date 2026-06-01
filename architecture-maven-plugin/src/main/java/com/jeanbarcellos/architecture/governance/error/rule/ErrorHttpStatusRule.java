package com.jeanbarcellos.architecture.governance.error.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.report.ValidationModule;
import com.jeanbarcellos.architecture.framework.rule.ItemRule;
import com.jeanbarcellos.architecture.framework.rule.RuleMetadata;
import com.jeanbarcellos.architecture.framework.rule.ValidationSeverity;
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
public class ErrorHttpStatusRule implements ItemRule<ErrorType> {

    private static final RuleMetadata METADATA = RuleMetadata.of(
            "ERR-007",
            "HTTP Status inválido",
            "O status HTTP associado ao erro deve estar entre 100 e 599.",
            "Utilize um código HTTP válido.",
            ValidationSeverity.ERROR);

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleMetadata metadata() {
        return METADATA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ErrorType target, ValidationContext context) {

        int status = target.getHttpStatus();

        if (status < 400 || status > 599) {

            context.addViolation(
                    ValidationModule.ERROR,
                    ValidationCategory.CONTRACT,
                    this,
                    target.getClass(),
                    "Invalid HTTP status for error: " + target.getCode());
        }
    }

}