package com.jeanbarcellos.core.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Representa a severidade operacional
 * de uma falha na aplicação.
 *
 * <p>
 * A severidade indica o impacto operacional
 * do erro no sistema.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@RequiredArgsConstructor
public enum ErrorSeverity {

    /**
     * Baixo impacto operacional.
     */
    LOW("low"),

    /**
     * Médio impacto operacional.
     */
    MEDIUM("medium"),

    /**
     * Alto impacto operacional.
     */
    HIGH("high"),

    /**
     * Impacto crítico.
     */
    CRITICAL("critical");

    /**
     * Código textual da severidade.
     */
    private final String code;
}