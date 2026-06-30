package com.jeanbarcellos.core.observability.spring.filter;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jeanbarcellos.core.observability.context.ObservabilityContext;
import com.jeanbarcellos.core.observability.exception.MissingCorrelationIdException;
import com.jeanbarcellos.core.observability.generator.CorrelationIdGenerator;
import com.jeanbarcellos.core.observability.properties.ObservabilityProperties;
import com.jeanbarcellos.core.observability.spring.context.ObservabilityContextManager;
import com.jeanbarcellos.core.observability.spring.context.ObservabilityContextResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Filter responsável por inicializar e finalizar o contexto de observabilidade
 * de cada requisição HTTP.
 *
 * <p>
 * Responsabilidades:
 * </p>
 *
 * <ul>
 * <li>Resolver o Correlation ID da requisição.</li>
 * <li>Gerar automaticamente um Correlation ID quando ausente.</li>
 * <li>Validar a obrigatoriedade do Correlation ID.</li>
 * <li>Inicializar o contexto de observabilidade.</li>
 * <li>Propagar o Correlation ID na resposta HTTP.</li>
 * <li>Finalizar o contexto ao término da requisição.</li>
 * </ul>
 *
 * <p>
 * Este filtro deve ser o primeiro da cadeia para garantir que todo o
 * processamento da requisição possua um contexto de observabilidade válido.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    private final ObservabilityProperties properties;

    private final CorrelationIdGenerator correlationIdGenerator;

    private final ObservabilityContextResolver contextResolver;

    private final ObservabilityContextManager contextManager;

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        // TODO Implementar utilização de ignored-paths.
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain)
            throws ServletException, IOException {

        final String correlationId = this.resolveCorrelationId(request);

        final ObservabilityContext context = this.contextResolver.resolve(correlationId);

        this.contextManager.initialize(context);

        if (this.properties.getCorrelation().isResponseHeaderEnabled()) {
            response.setHeader(this.properties.getCorrelation().getHeader(), correlationId);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            this.contextManager.clear();
        }
    }

    /**
     * Resolve o Correlation ID da requisição.
     *
     * <p>
     * Caso o header esteja ausente:
     * </p>
     *
     * <ul>
     * <li>gera automaticamente um novo Correlation ID quando permitido;</li>
     * <li>lança {@link MissingCorrelationIdException} quando obrigatório.</li>
     * </ul>
     *
     * @param request requisição HTTP.
     *
     * @return Correlation ID da requisição.
     */
    protected String resolveCorrelationId(final HttpServletRequest request) {

        final String correlationId = request.getHeader(this.properties.getCorrelation().getHeader());

        if (StringUtils.hasText(correlationId)) {
            return correlationId;
        }

        if (this.properties.getCorrelation().isRequired()) {
            throw new MissingCorrelationIdException();
        }

        return this.correlationIdGenerator.generate();
    }

}