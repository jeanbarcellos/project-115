package com.jeanbarcellos.core.observability.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ObservabilityConstants {

    public static final String DEFAULT_CORRELATION_HEADER = "correlation-id";

    public static final String MDC_CORRELATION_ID = "correlationId";

    public static final String MDC_TRACE_ID = "traceId";

    public static final String MDC_SPAN_ID = "spanId";

}