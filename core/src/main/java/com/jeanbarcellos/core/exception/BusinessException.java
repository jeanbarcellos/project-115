package com.jeanbarcellos.core.exception;

import java.util.Map;

import com.jeanbarcellos.core.error.ApiErrorType;

import lombok.Getter;

/**
 * Exceção utilizada para representar erros já preparados para exposição
 * externa.
 *
 * <p>
 * Diferente de {@link DomainException}, esta exceção já carrega dados
 * prontos para serem serializados como resposta da API.
 * </p>
 *
 * @author Jean Barcellos (jeanbarcellos@hotmail.com)
 */
@Getter
public class BusinessException extends ApplicationException {

    private final ApiErrorType type;
    private final Map<String, Object> properties;

    public BusinessException(ApiErrorType type, String detail) {
        this(type, detail, Map.of());
    }

    public BusinessException(ApiErrorType type, String detail, Map<String, Object> properties) {
        super(detail);
        this.type = type;
        this.properties = properties;
    }

}