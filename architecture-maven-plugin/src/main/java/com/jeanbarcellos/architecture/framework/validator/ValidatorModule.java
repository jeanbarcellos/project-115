package com.jeanbarcellos.architecture.framework.validator;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;

/**
 * Módulo de governança.
 *
 * <p>
 * Cada módulo encapsula um conjunto
 * de validações relacionadas a um
 * domínio arquitetural específico.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public interface ValidatorModule {

    /**
     * Executa as validações do módulo.
     *
     * @param context contexto compartilhado
     */
    void validate(ValidationContext context);

}