package com.jeanbarcellos.core.observability;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

/**
 * Evento padrão de erro para logs estruturados.
 */
@Getter
@Builder
public class ErrorLogEvent {

    private String event;         // "error" → facilita filtro (ELK, Datadog)
    private String errorCode;     // principal dimensão
    private int httpStatus;       // agrupamento técnico
    private boolean retryable;    // decisões automáticas

    private String message;       // leitura humana
    private String exception;     // stacktrace resumido

    private String correlationId; // rastreio

    private String path;          // contexto HTTP
    private String method;        // contexto HTTP

    private Instant timestamp;    // ordenação
}