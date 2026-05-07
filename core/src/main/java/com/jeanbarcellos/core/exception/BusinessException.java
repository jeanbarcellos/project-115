package com.jeanbarcellos.core.exception;

import java.util.Map;

import com.jeanbarcellos.core.error.ErrorType;

import lombok.Getter;

/**
 * Exceção utilizada para representar erros já preparados para exposição
 * externa.
 *
 * <p>
 * Representa um erro pronto para ser exposto externamente.
 * Diferente de {@link DomainException}, esta exceção já carrega dados
 * prontos para serem serializados como resposta da API.
 * </p>
 *
 * @author Jean Barcellos (jeanbarcellos@hotmail.com)
 */
@Getter
public class BusinessException extends ApplicationException {

    private final ErrorType type;
    private final Map<String, Object> properties;

    public BusinessException(ErrorType type, String detail) {
        this(type, detail, Map.of());
    }

    public BusinessException(ErrorType type, String detail, Map<String, Object> properties) {
        super(detail); // #TODO Seria message ou details?
        this.type = type;
        this.properties = properties;
    }

}