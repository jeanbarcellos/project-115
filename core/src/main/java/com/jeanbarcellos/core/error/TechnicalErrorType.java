package com.jeanbarcellos.core.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeração de erros técnicos comuns da aplicação.
 *
 * <p>
 * Esses erros são independentes de domínio e podem ser reutilizados
 * por qualquer módulo da aplicação.
 * </p>
 */
@Getter
@RequiredArgsConstructor
public enum TechnicalErrorType implements ErrorType {

    // ============================
    // GENERIC
    // ============================

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

    UNKNOWN_ERROR(
            "unknown-error",
            500,
            "Unknown error",
            true),

    // ============================
    // VALIDATION
    // ============================

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

    // ============================
    // RESOURCE
    // ============================

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

    // ============================
    // CONFLICT / CONCURRENCY
    // ============================

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

    // ============================
    // AUTH / SECURITY
    // ============================

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

    // ============================
    // INTEGRATION / EXTERNAL
    // ============================

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

    DEPENDENCY_FAILURE(
            "dependency-failure",
            424,
            "Dependency failure",
            true),

    // ============================
    // DATA / INFRASTRUCTURE
    // ============================

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

    CONNECTION_ERROR("connection-error",
            500,
            "Connection error",
            true),

    // ============================
    // RATE LIMIT / THROTTLING
    // ============================

    RATE_LIMIT_EXCEEDED(
            "rate-limit-exceeded",
            429,
            "Too many requests",
            true),

    // ============================
    // CACHE
    // ============================

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

    private final String code;
    private final int httpStatus;
    private final String title;
    private final boolean isRetryable;

}