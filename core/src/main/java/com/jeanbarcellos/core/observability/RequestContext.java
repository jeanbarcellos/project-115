package com.jeanbarcellos.core.observability;

import lombok.Builder;
import lombok.Getter;

/**
 * Contexto genérico da execução atual.
 *
 * Não possui dependência de framework.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@Builder
public class RequestContext {

    private final String correlationId;

    private final String traceId;

    private final String spanId;

    private final String source;

}