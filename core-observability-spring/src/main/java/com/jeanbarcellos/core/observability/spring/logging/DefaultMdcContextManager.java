package com.jeanbarcellos.core.observability.spring.logging;

import org.slf4j.MDC;

import com.jeanbarcellos.core.observability.constants.ObservabilityConstants;
import com.jeanbarcellos.core.observability.context.ObservabilityContext;
import com.jeanbarcellos.core.observability.context.ObservabilityContextProvider;

import lombok.RequiredArgsConstructor;

/**
 * Implementação padrão de {@link MdcContextManager}.
 *
 * <p>
 * Responsável por sincronizar o contexto de observabilidade com o MDC do
 * SLF4J, permitindo que os identificadores sejam automaticamente incluídos
 * nos logs.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@RequiredArgsConstructor
public class DefaultMdcContextManager implements MdcContextManager {

    private final ObservabilityContextProvider contextProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate() {
        ObservabilityContext context = this.contextProvider.getCurrentContext();

        put(ObservabilityConstants.MDC_CORRELATION_ID, context.getCorrelationId());
        put(ObservabilityConstants.MDC_TRACE_ID, context.getTraceId());
        put(ObservabilityConstants.MDC_SPAN_ID, context.getSpanId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        MDC.remove(ObservabilityConstants.MDC_CORRELATION_ID);
        MDC.remove(ObservabilityConstants.MDC_TRACE_ID);
        MDC.remove(ObservabilityConstants.MDC_SPAN_ID);
    }

    private void put(final String key, final String value) {
        if (value != null) {
            MDC.put(key, value);
        }
    }

}