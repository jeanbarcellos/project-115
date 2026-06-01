package com.jeanbarcellos.architecture.governance.error.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.report.ValidationModule;
import com.jeanbarcellos.architecture.framework.rule.ItemRule;
import com.jeanbarcellos.architecture.framework.rule.RuleMetadata;
import com.jeanbarcellos.architecture.framework.rule.ValidationSeverity;
import com.jeanbarcellos.core.error.ErrorType;

/**
 * Garante que códigos de erro estejam
 * preenchidos.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorCodeRule implements ItemRule<ErrorType> {

    /**
     * Metatados da Regra
     */
    private static final RuleMetadata METADATA = RuleMetadata.of(
            "ERR-003",
            "Código do erro obrigatório",
            "Todos os itens do catálogo ErrorType devem possuir código preenchido.",
            "Informe um código único  para o erro.",
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
    public void validate(
            ErrorType target,
            ValidationContext context) {

        String code = target.getCode();

        if (code == null || code.isBlank()) {

            context.addViolation(
                    ValidationModule.ERROR,
                    ValidationCategory.CONTRACT,
                    this,
                    target.getClass(),
                    ((Enum<?>) target).name(),
                    "Error code cannot be empty.");
        }
    }

}