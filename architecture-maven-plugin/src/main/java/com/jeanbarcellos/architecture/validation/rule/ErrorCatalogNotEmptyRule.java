package com.jeanbarcellos.architecture.validation.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;

/**
 * Garante que o catálogo possua constantes.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorCatalogNotEmptyRule implements ValidationRule<Class<?>> {

    @Override
    public void validate(
            Class<?> target,
            ValidationContext context)
            throws MojoExecutionException {

        Object[] constants = target.getEnumConstants();

        if (constants == null || constants.length == 0) {

            throw new MojoExecutionException(
                    "Empty error catalog: "
                            + target.getName());
        }
    }
}