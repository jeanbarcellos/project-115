package com.jeanbarcellos.core.observability;

import java.util.HashMap;
import java.util.Map;

import com.jeanbarcellos.core.error.ErrorType;

/**
 * Utilitário de observabilidade.
 */
public final class ObservabilityUtil {

    private ObservabilityUtil() {
    }

    public static Map<String, Object> buildErrorContext(ErrorType errorType) {

        Map<String, Object> context = new HashMap<>();

        context.put("errorCode", errorType.code());
        context.put("httpStatus", errorType.httpStatus());
        context.put("retryable", errorType.isRetryable());
        context.put("correlationId", CorrelationContext.get());

        return context;
    }
}