package com.jeanbarcellos.architecture.validation.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.core.error.ErrorType;

/**
 * Garante título preenchido.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorTitleRule
        implements ValidationRule<ErrorType> {

    @Override
    public void validate(
            ErrorType target,
            ValidationContext context)
            throws MojoExecutionException {

        if (target.getTitle() == null
                || target.getTitle().isBlank()) {

            throw new MojoExecutionException(
                    "Title cannot be empty: "
                            + target.getCode());
        }
    }
}