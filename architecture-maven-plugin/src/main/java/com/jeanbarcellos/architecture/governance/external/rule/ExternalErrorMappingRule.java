package com.jeanbarcellos.architecture.governance.external.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.report.ValidationModule;
import com.jeanbarcellos.architecture.framework.rule.ItemRule;
import com.jeanbarcellos.architecture.framework.rule.RuleMetadata;
import com.jeanbarcellos.core.error.ErrorType;
import com.jeanbarcellos.core.error.ExternalErrorType;
import com.jeanbarcellos.core.error.TechnicalErrorType;

/**
 * Garante que qualquer erro externo esteja
 * corretamente mapeado para um erro interno.
 *
 * <p>
 * Esta regra impede que falhas externas sejam
 * propagadas sem tradução para o catálogo oficial
 * de erros da plataforma.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ExternalErrorMappingRule
        implements ItemRule<ExternalErrorType> {

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleMetadata metadata() {

        return RuleMetadata.builder()
                .code("EXT-005")
                .name("Mapeamento obrigatório")
                .description("Todo erro externo deve possuir mapeamento para um ErrorType.")
                .recommendation("Associe o erro externo a um ErrorType interno.")
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ExternalErrorType target, ValidationContext context) {

        ErrorType errorType = target.getErrorType();

        if (errorType == null) {
            context.addViolation(
                    ValidationModule.EXTERNAL,
                    ValidationCategory.MAPPING,
                    this,
                    target.getClass(),
                    ((Enum<?>) target).name(),
                    "External error without internal mapping: " + target.getCode());
            return;
        }

        if (errorType == TechnicalErrorType.INTERNAL_ERROR) {
            context.addViolation(
                    ValidationModule.EXTERNAL,
                    ValidationCategory.MAPPING,
                    this,
                    target.getClass(),
                    ((Enum<?>) target).name(),
                    "External error cannot map to INTERNAL_ERROR: " + target.getCode());
        }
    }

}