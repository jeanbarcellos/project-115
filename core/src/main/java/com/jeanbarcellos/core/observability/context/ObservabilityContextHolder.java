package com.jeanbarcellos.core.observability.context;

/**
 * Armazena o contexto de observabilidade da thread corrente.
 *
 * <p>
 * Este componente representa a fonte de verdade da biblioteca para acesso ao
 * contexto atual da requisição. O contexto é armazenado utilizando
 * {@link ThreadLocal}, permitindo acesso durante todo o processamento da
 * requisição.
 * </p>
 *
 * <p>
 * O MDC é utilizado apenas como mecanismo de integração com frameworks de
 * logging, não devendo ser considerado como armazenamento oficial do contexto.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public final class ObservabilityContextHolder {

    private static final ThreadLocal<ObservabilityContext> HOLDER = new ThreadLocal<>();

    private ObservabilityContextHolder() {
    }

    /**
     * Obtém o contexto atual.
     *
     * @return contexto atual ou {@code null}.
     */
    public static ObservabilityContext get() {
        return HOLDER.get();
    }

    /**
     * Define o contexto atual.
     *
     * @param context contexto da requisição.
     */
    public static void set(final ObservabilityContext context) {
        HOLDER.set(context);

    }

    /**
     * Remove o contexto da thread corrente.
     */
    public static void clear() {
        HOLDER.remove();
    }

}