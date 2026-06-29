package com.jeanbarcellos.core.observability.spring.provider;

import com.jeanbarcellos.core.observability.constants.ObservabilityConstants;
import com.jeanbarcellos.core.observability.context.ObservabilityContext;
import com.jeanbarcellos.core.observability.context.ObservabilityContextProvider;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import org.slf4j.MDC;

public class DefaultObservabilityContextProvider
        implements ObservabilityContextProvider {

    @Override
    public ObservabilityContext getCurrentContext() {

        SpanContext spanContext = Span.current().getSpanContext();

        String traceId = null;
        String spanId = null;

        if (spanContext.isValid()) {

            traceId = spanContext.getTraceId();
            spanId = spanContext.getSpanId();

        }

        return ObservabilityContext.builder()
                .correlationId(MDC.get(ObservabilityConstants.MDC_CORRELATION_ID))
                .traceId(traceId)
                .spanId(spanId)
                .build();
    }

}