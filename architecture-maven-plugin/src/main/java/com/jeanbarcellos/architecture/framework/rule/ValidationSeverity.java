package com.jeanbarcellos.architecture.framework.rule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Níveis de severidade das regras arquiteturais.
 *
 * <p>
 * A severidade determina o impacto da
 * violação encontrada durante a validação.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@RequiredArgsConstructor
public enum ValidationSeverity {

    /**
     * Violação crítica.
     *
     * <p>
     * Deve interromper o build.
     * </p>
     */
    ERROR("ERROR"),

    /**
     * Violação não crítica.
     *
     * <p>
     * Não interrompe o build.
     * </p>
     */
    WARNING("WARNING");

    /**
     * Código da severidade.
     */
    private final String code;

}