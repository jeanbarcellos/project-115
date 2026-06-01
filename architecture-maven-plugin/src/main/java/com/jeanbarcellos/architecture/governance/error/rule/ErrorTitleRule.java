package com.jeanbarcellos.architecture.governance.error.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.report.ValidationModule;
import com.jeanbarcellos.architecture.framework.rule.ItemRule;
import com.jeanbarcellos.architecture.framework.rule.RuleMetadata;
import com.jeanbarcellos.core.error.ErrorType;

/**
 * Garante que o título do erro esteja preenchido.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorTitleRule implements ItemRule<ErrorType> {

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleMetadata metadata() {

        return RuleMetadata.builder()
                .code("ERR-006")
                .name("Título do erro obrigatório")
                .description("Todos os erros devem possuir um título preenchido.")
                .recommendation("Informe um título descritivo  para o erro.")
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ErrorType target, ValidationContext context) {

        if (target.getTitle() == null
                || target.getTitle().isBlank()) {

            context.addViolation(
                    ValidationModule.ERROR,
                    ValidationCategory.CONTRACT,
                    this,
                    target.getClass(),
                    ((Enum<?>) target).name(),
                    "Error title cannot be empty: " + target.getCode());
        }
    }

}