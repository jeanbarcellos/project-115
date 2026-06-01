package com.jeanbarcellos.architecture.governance.external.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.report.ValidationModule;
import com.jeanbarcellos.architecture.framework.rule.CatalogRule;
import com.jeanbarcellos.architecture.framework.rule.RuleMetadata;

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
public class ExternalErrorTypeMustBeEnumRule
        implements CatalogRule {

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleMetadata metadata() {

        return RuleMetadata.builder()
                .code("EXT-001")
                .name("ExternalErrorType deve ser enum")
                .description("Toda implementação da interface ExternalErrorType deve ser enum.")
                .recommendation("Converta a implementação para enum.")
                .build();
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