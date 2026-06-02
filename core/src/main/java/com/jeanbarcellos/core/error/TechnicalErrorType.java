package com.jeanbarcellos.core.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Catálogo global de erros técnicos reutilizáveis da plataforma.
 *
 * <p>
 * Estes erros representam falhas independentes
 * de domínio e podem ser utilizados por qualquer
 * módulo ou microsserviço.
 * </p>
 *
 * <p>
 * Este catálogo NÃO deve conter regras de negócio
 * específicas. Para isso devem ser utilizados
 * catálogos próprios de domínio
 * (ex: UserErrorType, WalletErrorType).
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@RequiredArgsConstructor
public enum TechnicalErrorType implements ErrorType {

    // ==============================================================================
    // GENERIC
    // ==============================================================================
    // Falhas técnicas genéricas que não pertencem a uma categoria
    // específica da plataforma.
    // ==============================================================================

    INTERNAL_ERROR(
            "internal-error",
            500,
            "Unexpected internal error",
            true),

    NOT_IMPLEMENTED(
            "not-implemented",
            501,
            "Feature not implemented",
            false),

    SERVICE_UNAVAILABLE(
            "service-unavailable",
            503,
            "Service temporarily unavailable",
            true),

    TIMEOUT(
            "timeout",
            504,
            "Operation timed out",
            true),

    // ==============================================================================
    // VALIDATION
    // ==============================================================================
    // Erros relacionados à validação de entradas,
    // parâmetros, payloads e contratos de API.
    // ==============================================================================

    /**
     * Erro de validação de entrada (cliente).
     */
    INPUT_VALIDATION_ERROR(
            "input-validation-error",
            422,
            "Invalid input",
            false),

    /**
     * Falha interna de validação (bug / inconsistência).
     */
    SYSTEM_VALIDATION_ERROR(
            "system-validation-error",
            500,
            "Internal validation failure",
            true),

    INVALID_PARAMETER(
            "invalid-parameter",
            400,
            "Invalid request parameter",
            false),

    MISSING_PARAMETER(
            "missing-parameter",
            400,
            "Missing required parameter",
            false),

    INVALID_FORMAT(
            "invalid-format",
            400,
            "Invalid format",
            false),

    // ==============================================================================
    // RESOURCE
    // Erros relacionados à localização, existência,
    // disponibilidade ou manipulação de recursos.
    // ==============================================================================

    RESOURCE_NOT_FOUND(
            "resource-not-found",
            404,
            "Resource not found",
            false),

    RESOURCE_ALREADY_EXISTS(
            "resource-already-exists",
            409,
            "Resource already exists",
            false),

    RESOURCE_LOCKED(
            "resource-locked",
            423,
            "Resource is locked",
            true),

    RESOURCE_GONE(
            "resource-gone",
            410,
            "Resource no longer available",
            false),

    RESOURCE_UNSUPPORTED_MEDIA_TYPE(
            "resource-unsupported-media-type",
            415,
            "Unsupported media type",
            false),

    RESOURCE_METHOD_NOT_ALLOWED(
            "resource-method-not-allowed",
            405,
            "Method not allowed.",
            false),

    MALFORMED_JSON(
            "malformed-json",
            400,
            "Malformed JSON",
            false),

    UNEXPECTED_ERROR(
            "unexpected-error",
            500,
            "Unexpected error",
            false),

    // ==============================================================================
    // CONFLICT / CONCURRENCY
    // ==============================================================================
    // Erros causados por concorrência, conflitos
    // de estado, versionamento e idempotência.
    // ==============================================================================

    CONFLICT("conflict",
            409,
            "Resource conflict",
            true),

    VERSION_CONFLICT(
            "version-conflict",
            409,
            "Version conflict",
            true),

    OPTIMISTIC_LOCK_ERROR(
            "optimistic-lock-error",
            409,
            "Optimistic lock failure",
            true),

    PESSIMISTIC_LOCK_ERROR(
            "pessimistic-lock-error",
            423,
            "Pessimistic lock failure",
            true),

    IDEMPOTENCY_CONFLICT(
            "idempotency-conflict",
            409,
            "Idempotency conflict",
            true),

    IDEMPOTENCY_PAYLOAD_MISMATCH(
            "idempotency-payload-mismatch",
            409,
            "Idempotency payload mismatch",
            false),

    // ==============================================================================
    // AUTH / SECURITY
    // ==============================================================================
    // Erros relacionados à autenticação,
    // autorização e segurança da aplicação.
    // ==============================================================================

    UNAUTHORIZED(
            "unauthorized",
            401,
            "Unauthorized",
            false),

    FORBIDDEN(
            "forbidden",
            403,
            "Forbidden",
            false),

    ACCESS_DENIED(
            "access-denied",
            403,
            "Access denied",
            false),

    TOKEN_EXPIRED(
            "token-expired",
            401,
            "Token expired",
            false),

    INVALID_TOKEN(
            "invalid-token",
            401,
            "Invalid token",
            false),

    // ==============================================================================
    // INTEGRATION / EXTERNAL
    // ==============================================================================
    // Falhas ocorridas durante comunicação com
    // sistemas externos ou dependências remotas.
    // ==============================================================================

    EXTERNAL_SERVICE_ERROR(
                    "external-service-error",
            502,
            "External service failure",
            true),

    EXTERNAL_SERVICE_TIMEOUT(
            "external-service-timeout",
            504,
            "External service timeout",
            true),

    EXTERNAL_SERVICE_UNAVAILABLE(
            "external-service-unavailable",
            503,
            "External service unavailable",
            true),

    EXTERNAL_SERVICE_BAD_RESPONSE(
            "external-service-bad-response",
            502,
            "Invalid response from external service",
            true),

    DEPENDENCY_FAILURE(
            "dependency-failure",
            424,
            "Dependency failure",
            true),

    // ==============================================================================
    // DATA / INFRASTRUCTURE
    // ==============================================================================
    // Falhas relacionadas à persistência, integridade serialização,
    // armazenamento e conectividade.
    // ==============================================================================

    DATABASE_ERROR(
                    "database-error",
            500,
            "Database error",
            true),

    DATA_INTEGRITY_VIOLATION(
            "data-integrity-violation",
            409,
            "Data integrity violation",
            false),

    SERIALIZATION_ERROR(
            "serialization-error",
            500,
            "Serialization error",
            false),

    DESERIALIZATION_ERROR(
            "deserialization-error",
            400,
            "Malformed request body",
            false),


    // ==============================================================================
    // INFRASTRUCTURE
    // ==============================================================================
    // Falhas relacionadas à infraestrutura,
    // conectividade, configuração, armazenamento de arquivos e mensageria.
    // ==============================================================================

    CONNECTION_ERROR(
            "connection-error",
            500,
            "Connection error",
            true),

    CONFIGURATION_ERROR(
            "configuration-error",
            500,
            "Application configuration error",
            false),

    FILE_STORAGE_ERROR(
            "file-storage-error",
            500,
            "File storage error",
            true),

    MESSAGE_BROKER_ERROR(
            "message-broker-error",
            500,
            "Message broker error",
            true),

    MESSAGE_PUBLISH_ERROR(
            "message-publish-error",
            500,
            "Message publish error",
            true),

    MESSAGE_CONSUME_ERROR(
            "message-consume-error",
            500,
            "Message consume error",
            true),

    // ==============================================================================
    // RATE LIMIT / THROTTLING
    // ==============================================================================
    // Limitações de consumo aplicadas para proteção
    // de recursos internos ou externos.
    // ==============================================================================

    RATE_LIMIT_EXCEEDED(
            "rate-limit-exceeded",
            429,
            "Too many requests",
            true),

    // ==============================================================================
    // CACHE
    // ==============================================================================
    // Falhas relacionadas à infraestrutura de cache
    // ou operações executadas sobre ela.
    // ==============================================================================

    CACHE_ERROR(
            "cache-error",
            500,
            "Cache error",
            true),

    CACHE_MISS(
            "cache-miss",
            404,
            "Cache miss",
            false);

    /**
     * Código único do erro.
     */
    private final String code;

    /**
     * Status HTTP associado ao erro.
     */
    private final int httpStatus;

    /**
     * Título curto e estável do erro.
     */
    private final String title;

    /**
     * Indica se uma nova tentativa é recomendada.
     */
    private final boolean isRetryable;

}