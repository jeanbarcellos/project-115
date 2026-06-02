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
@SuppressWarnings("java:S1948")
public class BusinessException extends ApplicationException {

    /**
     * Tipo do erro documentado no catálogo.
     */
    private final ErrorType errorType;

    /**
     * Propriedades adicionais para resposta ou logging.
     */
    private final Map<String, Object> properties;

    /**
     * Cria uma exceção de negócio.
     *
     * @param errorType tipo do erro
     * @param message   mensagem detalhada
     */
    public BusinessException(ErrorType errorType, String message) {
        this(errorType, message, Map.of());
    }

    /**
     * Cria uma exceção de negócio.
     *
     * @param errorType  tipo do erro
     * @param message    mensagem detalhada
     * @param properties propriedades adicionais
     */
    public BusinessException(ErrorType errorType, String message, Map<String, Object> properties) {
        super(message);
        this.errorType = errorType;
        this.properties = properties;
    }

}