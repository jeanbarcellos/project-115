package com.jeanbarcellos.architecture.validation.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.core.error.ErrorType;

/**
 * Garante status HTTP válido.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorHttpStatusRule
        implements ValidationRule<ErrorType> {

    @Override
    public void validate(
            ErrorType target,
            ValidationContext context)
            throws MojoExecutionException {

        int status = target.getHttpStatus();

        if (status < 400 || status > 599) {

            throw new MojoExecutionException(
                    "Invalid HTTP status: "
                            + target.getCode());
        }
    }
}