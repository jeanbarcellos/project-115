package com.jeanbarcellos.architecture.governance.external.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.report.ValidationModule;
import com.jeanbarcellos.architecture.framework.rule.ItemRule;
import com.jeanbarcellos.architecture.framework.rule.RuleMetadata;
import com.jeanbarcellos.architecture.framework.rule.ValidationSeverity;
import com.jeanbarcellos.core.error.ExternalErrorType;

/**
 * Garante que códigos externos estejam preenchidos.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ExternalErrorCodeRule implements ItemRule<ExternalErrorType> {

    private static final RuleMetadata METADATA = RuleMetadata.of(
            "EXT-002",
            "Código externo obrigatório",
            "Todo erro externo deve possuir um código preenchido.",
            "Informe o código retornado pelo provider.",
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
    public void validate(ExternalErrorType target, ValidationContext context) {

        String code = target.getCode();

        if (code == null || code.isBlank()) {
            context.addViolation(
                    ValidationModule.EXTERNAL,
                    ValidationCategory.CONTRACT,
                    this,
                    target.getClass(),
                    ((Enum<?>) target).name(),
                    "External error code cannot be empty.");
        }
    }

}