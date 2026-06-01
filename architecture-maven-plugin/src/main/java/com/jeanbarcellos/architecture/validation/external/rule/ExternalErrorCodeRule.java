package com.jeanbarcellos.architecture.validation.external.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.core.error.ExternalErrorType;
import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;

/**
 * Garante que códigos externos estejam preenchidos.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ExternalErrorCodeRule
        implements ValidationRule<ExternalErrorType> {

    /**
     * Executa a validação.
     *
     * @param target erro externo validado
     * @param context contexto compartilhado
     *
     * @throws MojoExecutionException quando o código estiver vazio
     */
    @Override
    public void validate(
            ExternalErrorType target,
            ValidationContext context)
            throws MojoExecutionException {

        String code = target.getCode();

        if (code == null || code.isBlank()) {

            throw new MojoExecutionException(
                    "External error code cannot be empty.");
        }
    }

}