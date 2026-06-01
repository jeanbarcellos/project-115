package com.jeanbarcellos.architecture.validation.external.rule;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;

/**
 * Garante que implementações de {@code ExternalErrorType}
 * sejam realizadas exclusivamente através de enums.
 *
 * <p>
 * Esta restrição mantém consistência com o modelo
 * adotado para os catálogos de erro da plataforma.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ExternalErrorTypeMustBeEnumRule
        implements ValidationRule<Class<?>> {

    /**
     * Executa a validação.
     *
     * @param target  implementação encontrada
     * @param context contexto compartilhado
     *
     * @throws MojoExecutionException quando a implementaçãonão for um enum
     */
    @Override
    public void validate(
            Class<?> target,
            ValidationContext context)
            throws MojoExecutionException {

        if (!target.isEnum()) {

            throw new MojoExecutionException(
                    "ExternalErrorType implementation must be enum: "
                            + target.getName());
        }
    }

}