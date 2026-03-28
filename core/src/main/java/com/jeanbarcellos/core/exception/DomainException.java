package com.jeanbarcellos.core.exception;

import java.util.Map;

import com.jeanbarcellos.core.error.ApiErrorType;

import lombok.Getter;

/**
 * Exceção de domínio utilizada para sinalizar violações de regras de negócio.
 *
 * <p>
 * Esta exceção é agnóstica a frameworks e não possui conhecimento de HTTP.
 * </p>
 *
 * @author Jean Barcellos (jeanbarcellos@hotmail.com)
 */
@Getter
public class DomainException extends ApplicationException {

    private final ApiErrorType type;
    private final Map<String, Object> context;

    public DomainException(ApiErrorType type, String detail) {
        this(type, detail, Map.of());
    }

    public DomainException(ApiErrorType type, String detail, Map<String, Object> context) {
        super(detail);
        this.type = type;
        this.context = context;
    }

}

