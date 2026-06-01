package com.jeanbarcellos.architecture.validation.error.rule;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.report.ValidationCategory;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;

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
public class ErrorTypeMustBeEnumRule
        implements ValidationRule<Class<?>> {

    /**
     * Executa a validação.
     *
     * @param target  implementação localizada
     * @param context contexto compartilhado
     */
    @Override
    public void validate(
            Class<?> target,
            ValidationContext context) {

        if (!target.isEnum()) {

            context.getReport().addViolation(
                    ValidationCategory.ERROR_TYPE,
                    "ErrorType implementation must be enum: " + target.getName());
        }
    }

}