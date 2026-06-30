package com.jeanbarcellos.core.observability.spring.context;

import com.jeanbarcellos.core.observability.context.ObservabilityContext;

/**
 * Resolve o contexto inicial de observabilidade.
 *
 * <p>
 * Esta implementação cria apenas o contexto inicial contendo
 * o Correlation ID.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ObservabilityContextResolver {

    public ObservabilityContext resolve(final String correlationId) {
        return ObservabilityContext.of(
                correlationId,
                null,
                null);

    }

}