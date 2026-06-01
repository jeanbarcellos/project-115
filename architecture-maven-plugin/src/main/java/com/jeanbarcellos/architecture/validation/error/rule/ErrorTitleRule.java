package com.jeanbarcellos.architecture.validation.error.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.core.error.ErrorType;
import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;

/**
 * Garante que o título do erro esteja preenchido.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorTitleRule
        implements ValidationRule<ErrorType> {

    /**
     * Executa a validação.
     *
     * @param target  erro validado
     * @param context contexto compartilhado
     *
     * @throws MojoExecutionException quando o título estiver vazio
     */
    @Override
    public void validate(
            ErrorType target,
            ValidationContext context)
            throws MojoExecutionException {

        if (target.getTitle() == null
                || target.getTitle().isBlank()) {

            throw new MojoExecutionException(
                    "Error title cannot be empty: "
                            + target.getCode());
        }
    }

}