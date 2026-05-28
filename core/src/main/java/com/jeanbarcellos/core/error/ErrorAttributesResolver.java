package com.jeanbarcellos.core.error;

/**
 * Responsável por resolver atributos
 * operacionais de um erro.
 *
 * <p>
 * Permite separar:
 * </p>
 *
 * <ul>
 *   <li>categoria arquitetural;</li>
 *   <li>criticidade operacional;</li>
 *   <li>nível de log;</li>
 *   <li>política de alerta.</li>
 * </ul>
 *
 * <p>
 * Isso evita acoplamento excessivo entre
 * taxonomia de erros e observabilidade.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public interface ErrorAttributesResolver {

    /**
     * Resolve atributos operacionais
     * do erro informado.
     *
     * @param category categoria do erro
     * @param errorType erro interno
     * @param exception exceção original
     * @return atributos operacionais resolvidos
     */
    ErrorAttributes resolve(
            ErrorCategory category,
            ErrorType errorType,
            Exception exception);
}