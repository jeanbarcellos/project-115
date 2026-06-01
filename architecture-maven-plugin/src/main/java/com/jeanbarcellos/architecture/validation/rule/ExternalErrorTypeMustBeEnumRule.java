package com.jeanbarcellos.architecture.validation.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;

/**
 * Garante que implementações de ExternalErrorType
 * sejam realizadas exclusivamente através de enums.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ExternalErrorTypeMustBeEnumRule
        implements ValidationRule<Class<?>> {

    @Override
    public void validate(
            Class<?> target,
            ValidationContext context)
            throws MojoExecutionException {

        if (!target.isEnum()) {

            throw new MojoExecutionException(
                    "ExternalErrorType implementation must be enum: "
                            + target.getName());
        }
    }
}