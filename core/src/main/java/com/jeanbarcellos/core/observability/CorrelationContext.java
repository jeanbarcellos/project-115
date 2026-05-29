package com.jeanbarcellos.core.observability;

import java.util.UUID;

/**
 * Contexto de correlação utilizado para rastreamento de requisições.
 *
 * <p>
 * Implementado com ThreadLocal para manter o identificador
 * durante o ciclo de vida da requisição.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public final class CorrelationContext {

    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    private CorrelationContext() {
    }

    public static void set(String id) {
        HOLDER.set(id);
    }

    public static String get() {
        return HOLDER.get();
    }

    public static String getOrCreate() {
        String id = HOLDER.get();

        if (id == null) {
            id = UUID.randomUUID().toString();
            HOLDER.set(id);
        }

        return id;
    }

    public static void clear() {
        HOLDER.remove();
    }

}