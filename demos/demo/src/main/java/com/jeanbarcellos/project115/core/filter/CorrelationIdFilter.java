package com.jeanbarcellos.project115.core.filter;

import static com.jeanbarcellos.project115.core.Constants.CORRELATION_ID_HEADER;
import static com.jeanbarcellos.project115.core.Constants.CORRELATION_ID_KEY;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String correlationId = getCorrelationId(request);
        log.info("correlationId: {}", correlationId);

        MDC.put(CORRELATION_ID_KEY, correlationId);

        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private String getCorrelationId(HttpServletRequest request) {
        return Optional
                .ofNullable(request.getHeader(CORRELATION_ID_HEADER))
                .orElse(UUID.randomUUID().toString());
    }
}