package com.jeanbarcellos.architecture.validation.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.report.ValidationCategory;

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

            context.getReport().addViolation(
                    ValidationCategory.ERROR_TYPE,
                    "Empty catalog detected: " + target.getName());
        }
    }

}