package com.jeanbarcellos.architecture.validation.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;

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
     *
     * @throws MojoExecutionException quando o catálogo estiver vazio
     */
    @Override
    public void validate(
            Class<?> target,
            ValidationContext context)
            throws MojoExecutionException {

        Object[] constants = target.getEnumConstants();

        if (constants == null || constants.length == 0) {

            throw new MojoExecutionException(
                    "Empty catalog detected: "
                            + target.getName());
        }
    }

}