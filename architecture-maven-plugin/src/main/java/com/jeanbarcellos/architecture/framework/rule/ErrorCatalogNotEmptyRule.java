package com.jeanbarcellos.architecture.framework.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;

/**
 * Garante que um catálogo possua pelo menos
 * um item declarado.
 *
 * <p>
 * Esta regra evita catálogos vazios que
 * provavelmente representam implementações
 * incompletas ou esquecidas durante o desenvolvimento.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorCatalogNotEmptyRule
        implements ValidationRule<Class<?>> {

    /**
     * Executa a validação.
     *
     * @param target  catálogo validado
     * @param context contexto compartilhado
     */
    @Override
    public void validate(Class<?> target, ValidationContext context) {

        Object[] constants = target.getEnumConstants();

        if (constants == null || constants.length == 0) {

            context.addViolation(
                    ValidationCategory.ERROR_TYPE,
                    target,
                    "Empty catalog detected: " + target.getName());
        }
    }

}