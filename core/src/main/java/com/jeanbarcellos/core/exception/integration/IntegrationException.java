package com.jeanbarcellos.core.exception.integration;

import java.util.Map;

import com.jeanbarcellos.core.error.ErrorType;
import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.exception.ApplicationException;
import com.jeanbarcellos.core.integration.ExternalErrorType;

import lombok.Getter;

/**
 * Exceção para falhas em integrações externas (REST, fila, etc).
 */
@Getter
@SuppressWarnings({ "java:S110", "java:S1948" })
public class IntegrationException extends ApplicationException {

    /**
     * Identificador lógico da integração utilizada.
     *
     * <p>
     * Recomenda-se utilizar o padrão:
     * </p>
     *
     * <pre>
     * {tipo}-{nome}
     * </pre>
     *
     * <p>
     * Exemplos:
     * </p>
     *
     * <ul>
     *   <li>rest-serpro</li>
     *   <li>rest-ibge</li>
     *   <li>grpc-ledger</li>
     *   <li>kafka-wallet-events</li>
     *   <li>redis-cache</li>
     *   <li>s3-documents</li>
     * </ul>
     */
    private final String service; // nome do serviço externo

    /**
     * Erro oficialmente retornado pelo provider externo.
     *
     * <p>
     * Pode ser {@code null} em falhas locais de infraestrutura/comunicação.
     * </p>
     */
    private final ExternalErrorType externalError;

    /**
     * Metadados adicionais úteis para troubleshooting,
     * observabilidade e logs estruturados.
     *
     * <p>
     * Exemplos:
     * </p>
     *
     * <ul>
     *   <li>requestId</li>
     *   <li>traceId</li>
     *   <li>headers</li>
     *   <li>endpoint</li>
     *   <li>partition</li>
     *   <li>bucket</li>
     * </ul>
     */
    private final Map<String, Object> metadata; // metadados (podendo ser header)

    /**
     * Cria uma nova exceção de integração.
     *
     * @param service       identificador lógico da integração
     * @param message       mensagem resumida da falha
     * @param metadata      metadados auxiliares
     * @param externalError erro retornado pelo provider externo
     * @param cause         causa raiz da falha
     */
    protected IntegrationException(
            String service,
            String message,
            Map<String, Object> metadata,
            ExternalErrorType externalError,
            Throwable cause) {

        super(message, cause);

        this.service = service;

        this.externalError = externalError;

        this.metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    /**
     * Erro interno associado à falha.
     *
     * <p>
     * Quando houver um erro externo mapeado,
     * o valor será obtido através do catálogo
     * do provider.
     * </p>
     *
     * <p>
     * Na ausência de um erro externo,
     * será retornado
     * {@link TechnicalErrorType#EXTERNAL_SERVICE_ERROR}.
     * </p>
     *
     * @return erro interno associado
     */
    public ErrorType getErrorType() {

        if (externalError != null) {
            return externalError.getErrorType();
        }

        return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
    }

}