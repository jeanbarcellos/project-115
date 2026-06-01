package com.jeanbarcellos.architecture.validation.error.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;
import com.jeanbarcellos.core.error.ErrorType;

/**
 * Garante unicidade global dos códigos
 * de erro da plataforma.
 *
 * <p>
 * Dois erros não podem compartilhar o
 * mesmo código, independentemente do módulo
 * em que foram declarados.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorCodeUniqueRule
        implements ValidationRule<ErrorType> {

    /**
     * Executa a validação.
     *
     * @param target  erro validado
     * @param context contexto compartilhado
     *
     * @throws MojoExecutionException quando existir duplicidade
     */
    @Override
    public void validate(
            ErrorType target,
            ValidationContext context)
            throws MojoExecutionException {

        if (!context.getErrorCodes().add(target.getCode())) {

            throw new MojoExecutionException(
                    "Duplicated error code detected: "
                            + target.getCode());
        }
    }

}