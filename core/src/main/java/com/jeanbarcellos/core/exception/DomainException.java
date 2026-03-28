package com.jeanbarcellos.core.exception;

import java.util.Map;

import com.jeanbarcellos.core.error.ApiErrorType;

import lombok.Getter;

/**
 * Exceção de domínio.
 *
 * <p>
 * Representa violações de regras internads do domínio..
 * NÃO contém qualquer informação de transporte (HTTP, API, etc).
 * </p>
 *
 * @author Jean Barcellos (jeanbarcellos@hotmail.com)
 */
@Getter
public class DomainException extends ApplicationException {

    /**
     * Contexto interno do erro (não exposto diretamente).
     */
    private final Map<String, Object> context;

    public DomainException(String message) {
        this(message, Map.of());
    }

    public DomainException(String message, Map<String, Object> context) {
        super(message);
        this.context = context;
    }

}
