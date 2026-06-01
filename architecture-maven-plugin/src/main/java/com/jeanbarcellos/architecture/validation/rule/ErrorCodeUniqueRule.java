package com.jeanbarcellos.architecture.validation.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.core.error.ErrorType;

/**
 * Garante unicidade global dos códigos.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorCodeUniqueRule implements ValidationRule<ErrorType> {

    @Override
    public void validate(
            ErrorType target,
            ValidationContext context)
            throws MojoExecutionException {

        if (!context.getErrorCodes().add(target.getCode())) {

            throw new MojoExecutionException(
                    "Duplicated error code: "
                            + target.getCode());
        }
    }
}