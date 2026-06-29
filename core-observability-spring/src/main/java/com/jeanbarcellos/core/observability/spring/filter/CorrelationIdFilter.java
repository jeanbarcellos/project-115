package com.jeanbarcellos.core.observability.spring.filter;

import com.jeanbarcellos.core.observability.constants.ObservabilityConstants;
import com.jeanbarcellos.core.observability.properties.ObservabilityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    private final ObservabilityProperties properties;

    public CorrelationIdFilter(
            ObservabilityProperties properties) {

        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String correlationId = request.getHeader(
                properties.getCorrelationHeader());

        if (correlationId == null && properties.isGenerateIfMissing()) {
            correlationId = UUID.randomUUID().toString();
        }

        try {

            if (correlationId != null) {
                MDC.put(ObservabilityConstants.MDC_CORRELATION_ID, correlationId);

                if (properties.isResponseHeaderEnabled()) {

                    response.setHeader(properties.getCorrelationHeader(), correlationId);
                }
            }

            filterChain.doFilter(request, response);

        } finally {

            MDC.remove(
                    ObservabilityConstants.MDC_CORRELATION_ID);
        }
    }

}