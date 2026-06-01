package com.jeanbarcellos.architecture.governance.error.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.report.ValidationModule;
import com.jeanbarcellos.architecture.framework.rule.CatalogRule;
import com.jeanbarcellos.architecture.framework.rule.RuleMetadata;
import com.jeanbarcellos.architecture.framework.rule.ValidationSeverity;

/**
 * Garante que implementações de ErrorType
 * sejam realizadas exclusivamente através
 * de enums.
 *
 * <p>
 * Esta regra garante consistência dos
 * catálogos de erro e evita implementações
 * arbitrárias utilizando classes ou records.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorTypeMustBeEnumRule implements CatalogRule {

    private static final RuleMetadata METADATA = RuleMetadata.of(
            "ERR-001",
            "ErrorType deve ser enum",
            "Toda implementação da interface ErrorType deve ser declarada como enum.",
            "Substitua implementações concretas por enums.",
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
    public void validate(Class<?> target, ValidationContext context) {

        if (!target.isEnum()) {
            context.addViolation(
                    ValidationModule.ERROR,
                    ValidationCategory.CATALOG,
                    this,
                    target,
                    "ErrorType implementation must be enum: " + target.getName());
        }
    }

}