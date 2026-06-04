package com.jeanbarcellos.core.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;

/**
 * Contrato responsável por traduzir uma exceção técnica
 * para um {@link TechnicalErrorType}.
 *
 * <p>
 * Implementações devem retornar {@code null}
 * quando não reconhecerem a exceção.
 * </p>
 *
 * @author Jean Barcellos
 */
public interface ErrorResolver {

    /**
     * Resolve uma exceção para um erro técnico.
     *
     * @param exception exceção capturada.
     * @return erro técnico correspondente ou {@code null}.
     */
    TechnicalErrorType resolve(Throwable exception);

}
