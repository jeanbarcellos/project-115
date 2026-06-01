package com.jeanbarcellos.architecture.validation.external.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;
import com.jeanbarcellos.core.error.ExternalErrorType;
import com.jeanbarcellos.core.error.TechnicalErrorType;

/**
 * Impede mapeamentos inválidos para erros internos.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ExternalErrorMappingRule
        implements ValidationRule<ExternalErrorType> {

    @Override
    public void validate(
            ExternalErrorType target,
            ValidationContext context)
            throws MojoExecutionException {

        if (target.getErrorType() == null) {

            throw new MojoExecutionException(
                    "External error without mapping: "
                            + target.getCode());
        }

        if (target.getErrorType() == TechnicalErrorType.INTERNAL_ERROR) {

            throw new MojoExecutionException(
                    "External error cannot map to INTERNAL_ERROR: "
                            + target.getCode());
        }
    }
}