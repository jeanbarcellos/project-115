package com.jeanbarcellos.architecture.validation.error.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
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
     *
     * @throws MojoExecutionException quando a implementação não for enum
     */
    @Override
    public void validate(
            Class<?> target,
            ValidationContext context)
            throws MojoExecutionException {

        if (!target.isEnum()) {

            throw new MojoExecutionException(
                    "ErrorType implementation must be enum: "
                            + target.getName());
        }
    }

}