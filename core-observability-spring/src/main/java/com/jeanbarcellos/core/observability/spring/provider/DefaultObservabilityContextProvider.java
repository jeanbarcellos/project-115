package com.jeanbarcellos.core.observability.spring.provider;

import com.jeanbarcellos.core.observability.context.ObservabilityContext;
import com.jeanbarcellos.core.observability.context.ObservabilityContextHolder;
import com.jeanbarcellos.core.observability.context.ObservabilityContextProvider;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;

/**
 * Implementação Spring do provider de contexto.
 *
 * <p>
 * Enriquece o contexto armazenado pela biblioteca com as informações
 * fornecidas pelo OpenTelemetry.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class DefaultObservabilityContextProvider implements ObservabilityContextProvider {

    @Override
    public ObservabilityContext getCurrentContext() {

        ObservabilityContext context = ObservabilityContextHolder.get();

        if (context == null) {
            context = ObservabilityContext.empty();
        }

        SpanContext spanContext = Span.current().getSpanContext();

        String traceId = null;
        String spanId = null;

        if (spanContext.isValid()) {
            traceId = spanContext.getTraceId();
            spanId = spanContext.getSpanId();
        }

        return ObservabilityContext.of(
                context.getCorrelationId(),
                traceId,
                spanId);

    }

}