package com.jeanbarcellos.core.observability.spring.context;

import com.jeanbarcellos.core.observability.context.ObservabilityContext;

/**
 * Gerencia o ciclo de vida do contexto de observabilidade da thread corrente.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public interface ObservabilityContextManager {

    void initialize(ObservabilityContext context);

    void clear();

}