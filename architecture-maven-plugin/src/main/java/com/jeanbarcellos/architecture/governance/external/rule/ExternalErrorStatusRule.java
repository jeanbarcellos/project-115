package com.jeanbarcellos.architecture.governance.external.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.report.ValidationModule;
import com.jeanbarcellos.architecture.framework.rule.ItemRule;
import com.jeanbarcellos.architecture.framework.rule.RuleMetadata;
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
        implements ItemRule<ExternalErrorType> {

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleMetadata metadata() {

        return RuleMetadata.builder()
                .code("EXT-004")
                .name("Status externo inválido")
                .description("O status retornado pelo provider deve ser válido.")
                .recommendation("Utilize um status HTTP entre 100 e 599.")
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ExternalErrorType target, ValidationContext context) {

        Integer status = target.getStatus();

        if (status == null) {
            context.addViolation(
                    ValidationModule.EXTERNAL,
                    ValidationCategory.CONTRACT,
                    this,
                    target.getClass(),
                    ((Enum<?>) target).name(),
                    "External error status cannot be null: " + target.getCode());
            return;
        }

        if (status < 100 || status > 599) {
            context.addViolation(
                    ValidationModule.EXTERNAL,
                    ValidationCategory.CONTRACT,
                    this,
                    target.getClass(),
                    ((Enum<?>) target).name(),
                    "Invalid external error status: " + target.getCode() + " -> " + status);
        }
    }

}