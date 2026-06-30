package com.jeanbarcellos.core.observability.spring.context;

import com.jeanbarcellos.core.observability.context.ObservabilityContext;
import com.jeanbarcellos.core.observability.context.ObservabilityContextHolder;
import com.jeanbarcellos.core.observability.spring.logging.MdcContextManager;

import lombok.RequiredArgsConstructor;

/**
 * Responsável pelo gerenciamento do ciclo de vida do contexto de
 * observabilidade da thread corrente.
 *
 * <p>
 * Esta implementação sincroniza o {@link ObservabilityContextHolder}
 * com o MDC utilizado pelo framework de logging.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@RequiredArgsConstructor
public class DefaultObservabilityContextManager implements ObservabilityContextManager {

    private final MdcContextManager mdcContextManager;

    /**
     * Inicializa o contexto da thread corrente.
     *
     * @param context contexto da requisição
     */
    public void initialize(final ObservabilityContext context) {
        ObservabilityContextHolder.set(context);
        this.mdcContextManager.populate();

    }

    /**
     * Finaliza o contexto da thread corrente.
     */
    public void clear() {
        this.mdcContextManager.clear();
        ObservabilityContextHolder.clear();
    }

}