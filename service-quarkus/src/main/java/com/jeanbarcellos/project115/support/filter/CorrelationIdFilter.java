package com.jeanbarcellos.project115.support.filter;

import static com.jeanbarcellos.core.Constants.CORRELATION_ID_HEADER;
import static com.jeanbarcellos.core.Constants.CORRELATION_ID_KEY;

import java.util.Optional;
import java.util.UUID;

import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;
import org.slf4j.MDC;

import com.jeanbarcellos.core.observability.CorrelationContext;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class CorrelationIdFilter {

    /**
     * Intercepta a requisição na entrada (Equivalente ao início do doFilterInternal)
     */
    @ServerRequestFilter(preMatching = true)
    public void filterRequest(ContainerRequestContext requestContext) {
        // 1. Tenta pegar o ID do cabeçalho, senão gera um novo
        String correlationId = Optional
                .ofNullable(requestContext.getHeaderString(CORRELATION_ID_HEADER))
                .orElse(UUID.randomUUID().toString());

        log.info("{}: {}", CORRELATION_ID_KEY, correlationId);

        // 2. Alimenta sua classe utilitária
        CorrelationContext.set(correlationId);

        // 3. Alimenta o MDC
        MDC.put(CORRELATION_ID_KEY, correlationId);

        // Armazena no contexto da requisição para recuperar no filtro de resposta
        requestContext.setProperty(CORRELATION_ID_KEY, correlationId);
    }

    /**
     * Intercepta a resposta (Equivalente ao bloco finally/pós-chain)
     */
    @ServerResponseFilter
    public void filterResponse(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String correlationId = (String) requestContext.getProperty(CORRELATION_ID_KEY);

        if (correlationId != null) {
            // 4. Adiciona o ID na resposta
            responseContext.getHeaders().add(CORRELATION_ID_HEADER, correlationId);
        }

        try {
            // A lógica de negócio já foi executada aqui
        } finally {
            // 5. ESSENCIAL: Limpa o contexto
            CorrelationContext.clear();
            MDC.remove(CORRELATION_ID_KEY);
        }
    }
}