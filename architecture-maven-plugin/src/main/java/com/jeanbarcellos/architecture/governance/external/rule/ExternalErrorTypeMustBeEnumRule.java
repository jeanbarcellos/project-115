package com.jeanbarcellos.architecture.governance.external.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.rule.ValidationRule;

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
        implements ValidationRule<Class<?>> {

    /**
     * Executa a validação.
     *
     * @param target  implementação encontrada
     * @param context contexto compartilhado
     */
    @Override
    public void validate(Class<?> target, ValidationContext context) {

        if (!target.isEnum()) {
            context.getReport().addViolation(
                    ValidationCategory.EXTERNAL_ERROR,
                    "ExternalErrorType implementation must be enum: " + target.getName());
        }
    }

}