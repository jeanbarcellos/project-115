package com.jeanbarcellos.project115.user.adapter.api.error;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jeanbarcellos.core.error.ApiError;
import com.jeanbarcellos.core.error.ApiErrorType;
import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.exception.ApplicationException;
import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.core.exception.ValidationException;
import com.jeanbarcellos.core.observability.CorrelationContext;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler global responsável por traduzir exceções para RFC 7807.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String MEDIA_TYPE_APPLICATION_PROBLEM_JSON = "application/problem+json";

    @Value("${api.problem.base-uri}")
    private String problemBaseUri;

    // ============================
    // DOMAIN → converte para BUSINESS
    // ============================

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomain(
            DomainException ex,
            HttpServletRequest request) {

        ApiErrorType type = ex.getType();

        ApiError error = buildError(
                type,
                ex.getMessage(),
                ex.getContext(),
                request.getRequestURI());

        log.warn("[domain-error] code={} correlationId={}",
                type.code(),
                CorrelationContext.get());

        return ResponseEntity.status(type.httpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(error);
    }

    // ============================
    // BUSINESS
    // ============================

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(
            BusinessException ex,
            HttpServletRequest request) {

        ApiErrorType type = ex.getType();

        ApiError error = buildError(
                type,
                ex.getMessage(),
                ex.getProperties(),
                request.getRequestURI());

        log.warn("[business-error] code={} correlationId={}",
                type.code(),
                CorrelationContext.get());

        return ResponseEntity.status(type.httpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(error);
    }
    // ========================================================================
    // VALIDATION → 422
    // ========================================================================

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidation(
            ValidationException ex,
            HttpServletRequest request) {

        TechnicalErrorType type = TechnicalErrorType.VALIDATION_ERROR;

        ApiError error = ApiError.builder()
                .type(resolveTypeUri(type))
                .title(type.title())
                .status(type.httpStatus())
                .detail(ex.getMessage()) // Mensagem customizada
                .instance(URI.create(request.getRequestURI()))
                .errors(ex.getErrors())
                .timestamp(Instant.now())
                .correlationId(CorrelationContext.get())
                .build();

        log.warn("[{}] correlationId={} errors={}",
                type.code(),
                CorrelationContext.get(),
                ex.getErrors());

        return ResponseEntity.status(type.httpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(error);
    }

    // ========================================================================
    // APPLICATION (fallback controlado)
    // ========================================================================

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiError> handleApplication(
            ApplicationException ex,
            HttpServletRequest request) {

        // Sem tipo explícito → vira erro interno
        TechnicalErrorType type = TechnicalErrorType.INTERNAL_ERROR;

        ApiError error = buildError(
                type,
                ex.getMessage(),
                request.getRequestURI());

        log.error("[{}] correlationId={} message={}",
                type.code(),
                CorrelationContext.get(),
                ex.getMessage(),
                ex);

        return ResponseEntity.status(type.httpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(error);
    }

    // ========================================================================
    // TECHNICAL
    // ========================================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleTechnical(
            Exception ex,
            HttpServletRequest request) {

        TechnicalErrorType type = TechnicalErrorResolver.resolve(ex);

        ApiError error = ApiError.builder()
                .type(resolveTypeUri(type))
                .title(type.title())
                .status(type.httpStatus())
                .detail("Unexpected error")
                .instance(URI.create(request.getRequestURI()))
                .timestamp(Instant.now())
                .correlationId(CorrelationContext.get())
                .properties(Map.of("retryable", type.isRetryable()))
                .build();

        log.error("[technical-error] code={} retryable={} correlationId={}",
                type.code(),
                type.isRetryable(),
                CorrelationContext.get(),
                ex);

        return ResponseEntity.status(type.httpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(error);
    }

    // ========================================================================
    // HELPERS
    // ========================================================================

    private ApiError buildError(
            ApiErrorType type,
            String detail,
            Map<String, Object> properties,
            String uri) {
        return ApiError.builder()
                .type(resolveTypeUri(type))
                .title(type.title())
                .status(type.httpStatus())
                .detail(detail)
                .instance(URI.create(uri))
                .timestamp(Instant.now())
                .correlationId(CorrelationContext.get())
                .properties(properties)
                .build();
    }

    private ApiError buildError(
            ApiErrorType type,
            String detail,
            String uri) {
        return ApiError.builder()
                .type(resolveTypeUri(type))
                .title(type.title())
                .status(type.httpStatus())
                .detail(detail)
                .instance(URI.create(uri))
                .timestamp(Instant.now())
                .correlationId(CorrelationContext.get())
                .build();
    }

    private URI resolveTypeUri(ApiErrorType type) {
        return URI.create(problemBaseUri + "/" + type.code());
    }
}