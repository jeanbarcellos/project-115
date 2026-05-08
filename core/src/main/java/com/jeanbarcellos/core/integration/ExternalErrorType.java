package com.jeanbarcellos.core.integration;

/**
 * Representa erros vindos de sistemas externos.
 * NÃO deve ser exposto na API.
 */
public interface ExternalErrorType {

    String getCode(); // código do provider

    String getMessage(); // mensagem original

    boolean isRetryable();   // decidido com base na documentação do provider
}