package com.jeanbarcellos.architecture.validation.error.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;
import com.jeanbarcellos.core.error.ErrorType;

/**
 * Garante que os status HTTP dos erros
 * estejam dentro da faixa válida para erros.
 *
 * <p>
 * Atualmente são aceitos apenas status
 * entre 400 e 599.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorHttpStatusRule
        implements ValidationRule<ErrorType> {

    /**
     * Executa a validação.
     *
     * @param target  erro validado
     * @param context contexto compartilhado
     *
     * @throws MojoExecutionException quando o status for inválido
     */
    @Override
    public void validate(
            ErrorType target,
            ValidationContext context)
            throws MojoExecutionException {

        int status = target.getHttpStatus();

        if (status < 400 || status > 599) {

            throw new MojoExecutionException(
                    "Invalid HTTP status for error: "
                            + target.getCode());
        }
    }

}