package com.jeanbarcellos.core.observability;

import lombok.Builder;
import lombok.Getter;

/**
 * Contexto de rastreamento distribuído.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@Builder
public class TraceContext {

    private final String traceId;

    private final String spanId;

}