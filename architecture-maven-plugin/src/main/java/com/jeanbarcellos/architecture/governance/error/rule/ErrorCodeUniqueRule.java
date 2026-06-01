package com.jeanbarcellos.architecture.governance.error.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.report.ValidationModule;
import com.jeanbarcellos.architecture.framework.rule.ItemRule;
import com.jeanbarcellos.architecture.framework.rule.RuleMetadata;
import com.jeanbarcellos.core.error.ErrorType;

/**
 * Garante unicidade global dos códigos
 * de erro da plataforma.
 *
 * <p>
 * Dois erros não podem compartilhar o
 * mesmo código, independentemente do módulo
 * em que foram declarados.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorCodeUniqueRule implements ItemRule<ErrorType> {

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleMetadata metadata() {

        return RuleMetadata.builder()
                .code("ERR-005")
                .name("Código do erro deve ser único")
                .description("Não é permitido reutilizar o mesmo código em múltiplos erros.")
                .recommendation("Defina um código exclusivo para o erro informado.")
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ErrorType target, ValidationContext context) {

        if (!context.getErrorCodes().add(target.getCode())) {

            context.addViolation(
                    ValidationModule.ERROR,
                    ValidationCategory.CONTRACT,
                    this,
                    target.getClass(),
                    ((Enum<?>) target).name(),
                    "Duplicated error code detected: " + target.getCode());
        }
    }

}