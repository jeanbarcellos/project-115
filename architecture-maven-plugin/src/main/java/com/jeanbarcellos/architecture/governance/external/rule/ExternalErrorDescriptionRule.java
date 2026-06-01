package com.jeanbarcellos.architecture.governance.external.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.report.ValidationModule;
import com.jeanbarcellos.architecture.framework.rule.ItemRule;
import com.jeanbarcellos.architecture.framework.rule.RuleMetadata;
import com.jeanbarcellos.core.error.ExternalErrorType;

/**
 * Garante que a descrição do erro externo
 * esteja preenchida.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ExternalErrorDescriptionRule
        implements ItemRule<ExternalErrorType> {

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleMetadata metadata() {

        return RuleMetadata.builder()
                .code("EXT-003")
                .name("Descrição obrigatória")
                .description("Todo erro externo deve possuir uma descrição.")
                .recommendation("Informe uma descrição clara do erro externo.")
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ExternalErrorType target, ValidationContext context) {

        String description = target.getDescription();

        if (description == null || description.isBlank()) {

            context.addViolation(
                    ValidationModule.EXTERNAL,
                    ValidationCategory.CONTRACT,
                    this,
                    target.getClass(),
                    ((Enum<?>) target).name(),
                    "External error description cannot be empty: " + target.getCode());
        }
    }

}