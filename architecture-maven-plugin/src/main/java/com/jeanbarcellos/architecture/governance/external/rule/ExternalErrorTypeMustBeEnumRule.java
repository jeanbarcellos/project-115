package com.jeanbarcellos.architecture.governance.external.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.report.ValidationModule;
import com.jeanbarcellos.architecture.framework.rule.CatalogRule;
import com.jeanbarcellos.architecture.framework.rule.RuleMetadata;
import com.jeanbarcellos.architecture.framework.rule.ValidationSeverity;

/**
 * Garante que implementações de {@code ExternalErrorType}
 * sejam realizadas exclusivamente através de enums.
 *
 * <p>
 * Esta restrição mantém consistência com o modelo
 * adotado para os catálogos de erro da plataforma.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ExternalErrorTypeMustBeEnumRule implements CatalogRule {

    private static final RuleMetadata METADATA = RuleMetadata.of(
            "EXT-001",
            "ExternalErrorType deve ser enum",
            "Toda implementação da interface ExternalErrorType deve ser enum.",
            "Converta a implementação para enum.",
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
    public void validate(Class<?> catalog, ValidationContext context) {

        if (!catalog.isEnum()) {
            context.addViolation(
                    ValidationModule.EXTERNAL,
                    ValidationCategory.CATALOG,
                    this,
                    catalog,
                    "ExternalErrorType implementation must be enum: " + catalog.getName());
        }
    }

}