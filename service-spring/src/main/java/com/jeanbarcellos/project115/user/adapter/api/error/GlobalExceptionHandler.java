package com.jeanbarcellos.project115.user.adapter.api.error;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jeanbarcellos.core.error.ErrorResponse;
import com.jeanbarcellos.core.error.ErrorType;
import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.ValidationError;
import com.jeanbarcellos.core.exception.ApplicationException;
import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.core.exception.DomainValidationException;
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

    // DOMAIN =================================================================

    // se chegou aqui → erro de arquitetura (não traduziu)
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomain(
            DomainException ex,
            HttpServletRequest request) {

        // ⚠️ fallback global (sem contexto específico de módulo)
        ErrorType type = TechnicalErrorType.SYSTEM_VALIDATION_ERROR;

        ErrorResponse error = ErrorResponse.builder()
                .type(resolveTypeUri(type))
                .title(type.title())
                .status(type.httpStatus())
                .detail(ex.getMessage())
                .instance(resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .properties(ex.getContext())
                .build();

        log.warn("[domain-error] code={} correlationId={}",
                type.code(),
                this.getCorrelationId());

        return ResponseEntity.status(type.httpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(error);
    }

    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<ErrorResponse> handleDomainValidation(
            DomainValidationException ex,
            HttpServletRequest request) {

        ErrorType type = TechnicalErrorType.INPUT_VALIDATION_ERROR;

        List<ValidationError> errors = ex.getViolations()
                .stream()
                .map(v -> ValidationError.of(
                        v.getField(),
                        v.getMessage(),
                        v.getRejectedValue()))
                .toList();

        ErrorResponse error = ErrorResponse.builder()
                .type(resolveTypeUri(type))
                .title(type.title())
                .status(type.httpStatus())
                .detail(ex.getMessage()) // Mensagem customizada
                .instance(resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .errors(errors) // Campo customizado de erros
                .build();

        log.warn("[validation-error] correlationId={} errors={}",
                type.code(),
                this.getCorrelationId(),
                errors);

        return ResponseEntity.status(type.httpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(error);
    }

    // BUSINESS ===============================================================

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(
            BusinessException ex,
            HttpServletRequest request) {

        ErrorType type = ex.getType();

        ErrorResponse error = ErrorResponse.builder()
                .type(resolveTypeUri(type))
                .title(type.title())
                .status(type.httpStatus())
                .detail(ex.getMessage()) // Mensagem customizada da exception
                .instance(resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .properties(ex.getProperties()) // Propriedades extras/contextos
                .build();

        log.warn("[business-error] code={} correlationId={}",
                type.code(),
                this.getCorrelationId());

        return ResponseEntity.status(type.httpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(error);
    }

    // VALIDATION → 422 =======================================================

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex,
            HttpServletRequest request) {

        TechnicalErrorType type = TechnicalErrorType.INPUT_VALIDATION_ERROR;

        ErrorResponse error = ErrorResponse.builder()
                .type(resolveTypeUri(type))
                .title(type.title())
                .status(type.httpStatus())
                .detail(ex.getMessage()) // Mensagem customizada
                .instance(resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .errors(ex.getErrors()) // Campo customizado de erros
                .build();

        log.warn("[validation-error] correlationId={} errors={}",
                type.code(),
                this.getCorrelationId(),
                ex.getErrors());

        return ResponseEntity.status(type.httpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(error);
    }

    // APPLICATION (fallback controlado) ======================================

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplication(
            ApplicationException ex,
            HttpServletRequest request) {

        // Sem tipo explícito → vira erro interno
        TechnicalErrorType type = TechnicalErrorType.INTERNAL_ERROR;

        ErrorResponse error = ErrorResponse.builder()
                .type(resolveTypeUri(type))
                .title(type.title())
                .status(type.httpStatus())
                .detail(ex.getMessage()) // Mensagem customizada da exception
                .instance(resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .build();

        log.error("[{}] correlationId={} message={}",
                type.code(),
                this.getCorrelationId(),
                ex.getMessage(),
                ex);

        return ResponseEntity.status(type.httpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(error);
    }

    // GENERIC / TECHNICAL ====================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleTechnical(
            Exception ex,
            HttpServletRequest request) {

        TechnicalErrorType type = TechnicalErrorResolver.resolve(ex);

        ErrorResponse error = ErrorResponse.builder()
                .type(resolveTypeUri(type))
                .title(type.title())
                .status(type.httpStatus())
                .detail("Unexpected error")
                .instance(this.resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .properties(Map.of("retryable", type.isRetryable()))
                .build();

        log.error("[technical-error] code={} retryable={} correlationId={}",
                type.code(),
                type.isRetryable(),
                this.getCorrelationId(),
                ex);

        return ResponseEntity.status(type.httpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(error);
    }

    // HELPERS ================================================================

    private ResponseEntity<ErrorResponse> buildResponse(
            ErrorType errorType,
            String detail,
            HttpServletRequest request,
            List<ValidationError> errors,
            Map<String, Object> properties) {

        ErrorResponse response = ErrorResponse.builder()
                .type(resolveTypeUri(errorType))
                .title(errorType.title())
                .status(errorType.httpStatus())
                .detail(detail)
                .instance(this.resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .errors(errors)
                .properties(this.buildProperties(errorType, properties))
                .build();

        return ResponseEntity.status(errorType.httpStatus()).body(response);
    }

    private URI resolveTypeUri(ErrorType type) {
        return URI.create(this.problemBaseUri + "/" + type.code());
    }

    private URI resolveInstance(HttpServletRequest request) {
        return URI.create(request.getRequestURI());
    }

    private String getCorrelationId() {
        return CorrelationContext.get();
    }

    private Map<String, Object> buildProperties(
            ErrorType errorType,
            Map<String, Object> custom) {

        Map<String, Object> base = Map.of(
                "errorCode", errorType.code(),
                "retryable", errorType.isRetryable());

        if (custom == null || custom.isEmpty()) {
            return base;
        }

        Map<String, Object> merged = new HashMap<>(base);
        merged.putAll(custom);

        return merged;
    }

    // LOGGING ================================================================

    private void log(ErrorType errorType, Exception ex) {

        if (errorType.httpStatus() >= 500) {
            log.error("[{}] {}", errorType.code(), ex.getMessage(), ex);
        } else {
            log.warn("[{}] {}", errorType.code(), ex.getMessage());
        }
    }
}