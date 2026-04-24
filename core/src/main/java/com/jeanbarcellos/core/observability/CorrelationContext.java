package com.jeanbarcellos.core.observability;

import java.util.UUID;

/**
 * Contexto de correlação utilizado para rastreamento de requisições.
 *
 * <p>
 * Implementado com ThreadLocal para manter o identificador
 * durante o ciclo de vida da requisição.
 * </p>
 */
public final class CorrelationContext {

    private static final ThreadLocal<String> CORRELATION_ID = new ThreadLocal<>();

    private CorrelationContext() {
    }

    public static void set(String id) {
        CORRELATION_ID.set(id);
    }

    public static String get() {
        return CORRELATION_ID.get();
    }

    public static String getOrCreate() {
        String id = CORRELATION_ID.get();

        if (id == null) {
            id = UUID.randomUUID().toString();
            CORRELATION_ID.set(id);
        }

        return id;
    }

    public static void clear() {
        CORRELATION_ID.remove();
    }

}