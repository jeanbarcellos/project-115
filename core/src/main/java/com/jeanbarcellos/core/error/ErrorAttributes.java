package com.jeanbarcellos.core.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Representa atributos operacionais
 * de observabilidade de um erro.
 *
 * <p>
 * Estes atributos NÃO representam
 * a classificação arquitetural do erro,
 * mas sim o impacto operacional específico
 * daquela ocorrência.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@RequiredArgsConstructor
public class ErrorAttributes {

    /**
     * Severidade operacional da falha.
     */
    private final ErrorSeverity severity;

    /**
     * Nível de log recomendado.
     */
    private final ErrorLogLevel logLevel;

    /**
     * Indica se a falha deve gerar alerta operacional.
     */
    private final boolean alertable;

    public static ErrorAttributes of(ErrorSeverity severity, ErrorLogLevel logLevel, boolean alertable) {
        return new ErrorAttributes(severity, logLevel, alertable);
    }
}