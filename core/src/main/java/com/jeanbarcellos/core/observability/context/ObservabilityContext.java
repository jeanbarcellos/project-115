package com.jeanbarcellos.core.observability.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ObservabilityContext {

    private final String correlationId;

    private final String traceId;

    private final String spanId;

}