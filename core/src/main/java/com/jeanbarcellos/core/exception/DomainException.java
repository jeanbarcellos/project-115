package com.jeanbarcellos.core.exception;

import java.util.Map;

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

    private final String code;

    /**
     * Contexto interno do erro (não exposto diretamente).
     */
    private final Map<String, Object> context;

    public DomainException(String message) {
        this(message, Map.of());
    }

    // public DomainException(String message, Map<String, Object> context) {
    //     super(message);
    //     this.context = context;
    // }

    public DomainException(String code, Map<String, Object> context) {
        super(code);
        this.code = code;
        this.context = context;
    }

}
