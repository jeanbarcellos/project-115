package com.jeanbarcellos.project115.infra.filter;

import static com.jeanbarcellos.core.Constants.CORRELATION_ID_HEADER;
import static com.jeanbarcellos.core.Constants.CORRELATION_ID_KEY;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jeanbarcellos.core.observability.CorrelationContext;

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

        // 1. Tenta pegar o ID do cabeçalho, senão gera um novo
        String correlationId = getCorrelationId(request);
        log.info("{}: {}", CORRELATION_ID_KEY, correlationId);

        // 2. Alimenta sua classe utilitária
        CorrelationContext.set(correlationId);

        // 3. Opcional: Alimenta o MDC do Logback para aparecer nos logs automaticamente
        MDC.put(CORRELATION_ID_KEY, correlationId);

        // 4. Adiciona o ID na resposta para facilitar o debug pelo cliente
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 5. ESSENCIAL: Limpa o contexto para evitar vazamento de memória
            CorrelationContext.clear();
            MDC.remove(CORRELATION_ID_KEY);
        }
    }

    private String getCorrelationId(HttpServletRequest request) {
        return Optional
                .ofNullable(request.getHeader(CORRELATION_ID_HEADER))
                .orElse(UUID.randomUUID().toString());
    }
}