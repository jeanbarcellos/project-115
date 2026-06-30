package com.jeanbarcellos.core.observability.context;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ObservabilityContext {

    private final String correlationId;

    private final String traceId;

    private final String spanId;

    /**
     * Cria uma nova instância de contexto.
     *
     * @param correlationId correlation id
     * @param traceId       trace id
     * @param spanId        span id
     * @return contexto
     */
    public static ObservabilityContext of(
            final String correlationId,
            final String traceId,
            final String spanId) {

        return new ObservabilityContext(correlationId, traceId, spanId);
    }

    public static ObservabilityContext empty() {
        return new ObservabilityContext(
                null,
                null,
                null);

    }

    public ObservabilityContext withTrace(final String traceId, final String spanId) {
        return new ObservabilityContext(
                this.correlationId,
                traceId,
                spanId);
    }

}