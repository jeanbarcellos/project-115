package com.jeanbarcellos.architecture.validation.error.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;

/**
 * Garante que ErrorType seja implementado somente por enums.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorTypeMustBeEnumRule implements ValidationRule<Class<?>> {

    @Override
    public void validate(
            Class<?> target,
            ValidationContext context)
            throws MojoExecutionException {

        if (!target.isEnum()) {

            throw new MojoExecutionException(
                    "ErrorType implementation must be enum: " + target.getName());
        }
    }
}