package com.jeanbarcellos.core.observability.spring.logging;

/**
 * Responsável por sincronizar o contexto de observabilidade com o
 * mecanismo de logging utilizado pela aplicação.
 *
 * <p>
 * A implementação padrão utiliza o SLF4J MDC, porém esta interface permite
 * substituir facilmente essa estratégia caso outro framework de logging seja
 * utilizado futuramente.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public interface MdcContextManager {

    /**
     * Popula o contexto do mecanismo de logging.
     *
     * @param context contexto da requisição
     */
    void populate();

    /**
     * Remove do mecanismo de logging apenas as informações adicionadas pela
     * biblioteca de observabilidade.
     */
    void clear();

}