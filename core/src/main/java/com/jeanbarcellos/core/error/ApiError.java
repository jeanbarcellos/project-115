package com.jeanbarcellos.core.error;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Representa um erro padronizado conforme a RFC 7807 (Problem Details for HTTP
 * APIs).
 *
 * <p>
 * Esta classe é agnóstica a frameworks e não contém dependências externas.
 * O campo {@code type} (URI) NÃO é definido aqui propositalmente, sendo
 * responsabilidade
 * do adapter (ex: handler HTTP) resolver a URL do problema.
 * </p>
 *
 * <p>
 * Campos adicionais como {@code errors}, {@code properties},
 * {@code correlationId} e
 * {@code timestamp} são extensões permitidas pela RFC.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    // RFC 7807  ********************************

    private URI type;

    /**
     * Título curto e estável do problema.
     */
    private String title;

    /**
     * Status HTTP associado ao erro.
     */
    private int status;

    /**
     * Descrição detalhada da ocorrência específica.
     */
    private String detail;

    /**
     * URI da requisição que originou o erro.
     */
    private URI instance;

    // Observabilidade **************************

    /**
     * Identificador de correlação para rastreamento.
     */
    private String correlationId;

    /**
     * Timestamp da ocorrência do erro.
     */
    private Instant timestamp;

    // Validação entrada ************************

    /**
     * Lista de erros de validação (quando aplicável).
     */
    private List<ValidationError> errors;

    // Extensões livres *************************

    /**
     * Extensões adicionais permitidas pela RFC 7807.
     */
    private Map<String, Object> properties;
}