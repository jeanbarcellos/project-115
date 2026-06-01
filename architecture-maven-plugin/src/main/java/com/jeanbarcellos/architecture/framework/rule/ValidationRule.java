package com.jeanbarcellos.architecture.framework.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;

/**
 * Contrato base para regras arquiteturais.
 *
 * <p>
 * Cada implementação representa uma regra
 * isolada e reutilizável.
 * </p>
 *
 * <p>
 * As regras devem ser pequenas, focadas
 * e possuir apenas uma responsabilidade.
 * </p>
 *
 * @param <T> tipo validado
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public interface ValidationRule<T> {

    /**
     * Executa a validação.
     *
     * @param target  objeto validado
     * @param context contexto compartilhado
     */
    void validate(T target, ValidationContext context);

}