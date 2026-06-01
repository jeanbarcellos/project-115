package com.jeanbarcellos.architecture.validation.external.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;
import com.jeanbarcellos.core.error.ExternalErrorType;

/**
 * Garante que a descrição do erro externo
 * esteja preenchida.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ExternalErrorDescriptionRule
        implements ValidationRule<ExternalErrorType> {

    /**
     * Executa a validação.
     *
     * @param target  erro externo validado
     * @param context contexto compartilhado
     *
     * @throws MojoExecutionException quando a descrição estiver vazia
     */
    @Override
    public void validate(
            ExternalErrorType target,
            ValidationContext context)
            throws MojoExecutionException {

        String description = target.getDescription();

        if (description == null || description.isBlank()) {

            throw new MojoExecutionException(
                    "External error description cannot be empty: "
                            + target.getCode());
        }
    }

}