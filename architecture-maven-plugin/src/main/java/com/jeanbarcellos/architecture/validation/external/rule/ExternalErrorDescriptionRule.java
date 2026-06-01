package com.jeanbarcellos.architecture.validation.external.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;
import com.jeanbarcellos.core.error.ExternalErrorType;

/**
 * Valida título do erro externo.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ExternalErrorDescriptionRule
        implements ValidationRule<ExternalErrorType> {

    @Override
    public void validate(
            ExternalErrorType target,
            ValidationContext context)
            throws MojoExecutionException {

        if (target.getDescription() == null
                || target.getDescription().isBlank()) {

            throw new MojoExecutionException(
                    "External error title cannot be empty: "
                            + target.getCode());
        }
    }
}