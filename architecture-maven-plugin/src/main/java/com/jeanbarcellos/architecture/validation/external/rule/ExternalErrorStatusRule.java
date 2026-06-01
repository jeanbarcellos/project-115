package com.jeanbarcellos.architecture.validation.external.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;
import com.jeanbarcellos.core.error.ExternalErrorType;

/**
 * Garante que o status retornado pelo provider
 * externo seja válido.
 *
 * <p>
 * São aceitos valores entre 100 e 599.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ExternalErrorStatusRule
        implements ValidationRule<ExternalErrorType> {

    /**
     * Executa a validação.
     *
     * @param target  erro externo validado
     * @param context contexto compartilhado
     *
     * @throws MojoExecutionException quando o status for inválido
     */
    @Override
    public void validate(
            ExternalErrorType target,
            ValidationContext context)
            throws MojoExecutionException {

        Integer status = target.getStatus();

        if (status == null) {

            throw new MojoExecutionException(
                    "External error status cannot be null: " + target.getCode());
        }

        if (status < 100 || status > 599) {
            throw new MojoExecutionException("Invalid external error status: " + target.getCode() + " -> " + status);
        }
    }

}