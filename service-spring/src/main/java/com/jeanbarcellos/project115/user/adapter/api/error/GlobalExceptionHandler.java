package com.jeanbarcellos.project115.user.adapter.api.error;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
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
import com.jeanbarcellos.core.observability.ErrorLogEvent;

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

        // TODO Context do dominio não deveria ser repassado para client
        Map<String, Object> properties = ObjectUtils.isNotEmpty(ex.getContext()) ? ex.getContext(): null;

        ErrorResponse error = ErrorResponse.builder()
                .type(resolveTypeUri(type))
                .title(type.getTitle())
                .status(type.getHttpStatus())
                .detail(ex.getMessage()) // Mensagem customizada da exception
                .instance(resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .properties(properties) // Propriedades extras/contextos
                .build();

        this.log("domain", type, ex, ex.getMessage());

        return ResponseEntity.status(type.getHttpStatus())
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
                .map(violation -> ValidationError.of(
                        violation.getField(),
                        violation.getMessage(),
                        violation.getRejectedValue()))
                .toList();

        ErrorResponse error = ErrorResponse.builder()
                .type(resolveTypeUri(type))
                .title(type.getTitle())
                .status(type.getHttpStatus())
                .detail(ex.getMessage()) // Mensagem customizada da exception
                .instance(resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .errors(errors) // Campo customizado de erros
                .build();

        this.log("validation", type, ex, ex.getMessage());

        return ResponseEntity.status(type.getHttpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(error);
    }

    // BUSINESS ===============================================================

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(
            BusinessException ex,
            HttpServletRequest request) {

        ErrorType type = ex.getType();

        Map<String, Object> properties = ObjectUtils.isNotEmpty(ex.getProperties()) ? ex.getProperties(): null;

        ErrorResponse error = ErrorResponse.builder()
                .type(resolveTypeUri(type))
                .title(type.getTitle())
                .status(type.getHttpStatus())
                .detail(ex.getMessage()) // Mensagem customizada da exception
                .instance(resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .properties(properties) // Propriedades extras/contextos
                .build();

        this.log("business", type, ex, ex.getMessage());

        return ResponseEntity.status(type.getHttpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(error);
    }

    // VALIDATION → 422 =======================================================

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex,
            HttpServletRequest request) {

        TechnicalErrorType type = TechnicalErrorType.INPUT_VALIDATION_ERROR;

        List<ValidationError> errors = ex.getErrors();

        ErrorResponse error = ErrorResponse.builder()
                .type(resolveTypeUri(type))
                .title(type.getTitle())
                .status(type.getHttpStatus())
                .detail(ex.getMessage()) // Mensagem customizada da exception
                .instance(resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .errors(errors) // Campo customizado de erros
                .build();

        this.log("validation", type, ex, ex.getMessage());

        return ResponseEntity.status(type.getHttpStatus())
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
                .title(type.getTitle())
                .status(type.getHttpStatus())
                .detail(ex.getMessage()) // Mensagem customizada da exception
                .instance(resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .build();

        this.log("technical", type, ex, ex.getMessage());

        return ResponseEntity.status(type.getHttpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(error);
    }


    // GENERIC / TECHNICAL ====================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleTechnical(
            Exception ex,
            HttpServletRequest request) {

        TechnicalErrorType type = TechnicalErrorResolver.resolve(ex);
        String detail = "Unexpected error";

        ErrorResponse error = ErrorResponse.builder()
                .type(resolveTypeUri(type))
                .title(type.getTitle())
                .status(type.getHttpStatus())
                .detail(detail)
                .instance(this.resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .properties(Map.of("retryable", type.isRetryable()))
                .build();

        this.log("technical", type, ex, detail);

        return ResponseEntity.status(type.getHttpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(error);
    }




    // RESOLVERS ==============================================================

    private URI resolveTypeUri(ErrorType type) {
        return URI.create(this.problemBaseUri + "/" + type.getCode());
    }

    private URI resolveInstance(HttpServletRequest request) {
        return URI.create(request.getRequestURI());
    }

    private String getCorrelationId() {
        return CorrelationContext.get();
    }


    // BUILDERS ===============================================================

    private ResponseEntity<ErrorResponse> buildResponse(
            ErrorType errorType,
            String detail,
            HttpServletRequest request,
            List<ValidationError> errors,
            Map<String, Object> properties) {

        ErrorResponse response = ErrorResponse.builder()
                .type(resolveTypeUri(errorType))
                .title(errorType.getTitle())
                .status(errorType.getHttpStatus())
                .detail(detail)
                .instance(this.resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .errors(errors)
                .properties(this.buildProperties(errorType, properties))
                .build();

        return ResponseEntity.status(errorType.getHttpStatus()).body(response);
    }



    private Map<String, Object> buildProperties(
            ErrorType errorType,
            Map<String, Object> custom) {

        Map<String, Object> base = Map.of(
                "errorCode", errorType.getCode(),
                "retryable", errorType.isRetryable());

        if (custom == null || custom.isEmpty()) {
            return base;
        }

        Map<String, Object> merged = new HashMap<>(base);
        merged.putAll(custom);

        return merged;
    }

    private Map<String, Object> buildLogContext(
            ErrorType type,
            String correlationId,
            Map<String, Object> custom) {

        Map<String, Object> context = new HashMap<>();

        context.put("errorCode", type.getCode());
        context.put("httpStatus", type.getHttpStatus());
        context.put("retryable", type.isRetryable());
        context.put("correlationId", correlationId);

        if (custom != null) {
            context.putAll(custom);
        }

        return context;
    }

    private ErrorLogEvent buildLogEvent(
            ErrorType type,
            String detail,
            Exception ex,
            HttpServletRequest request,
            String correlationId) {

        return ErrorLogEvent.builder()
                .event("error")
                .errorCode(type.getCode())
                .httpStatus(type.getHttpStatus())
                .retryable(type.isRetryable())
                .message(detail)
                .exception(ex != null ? ex.getClass().getSimpleName() : null)
                .correlationId(correlationId)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(Instant.now())
                .build();
    }



    // LOGGING ================================================================

    private void log(String category, ErrorType type, Exception ex, String detail) {

        String correlationId = this.getCorrelationId();

        // Erros não técnicos não é necessário logar
        String pattern = "[error][{}] code={} status={} retryable={} correlationId={} message={}";

        if (type.getHttpStatus() >= 500) {
            log.error(pattern,
                    category,
                    type.getCode(),
                    type.getHttpStatus(),
                    type.isRetryable(),
                    correlationId,
                    detail,
                    ex);
        } else {
            log.warn(pattern,
                    category,
                    type.getCode(),
                    type.getHttpStatus(),
                    type.isRetryable(),
                    correlationId,
                    detail);
        }
    }

    private void log(String category, ErrorType type, Exception ex, String detail, Map<String, Object> context) {

        String correlationId = this.getCorrelationId();
        Map<String, Object> properties = ObjectUtils.isNotEmpty(context) ? context : null;

        // Erros não técnicos não é necessário logar
        String pattern = "[error][{}] code={} status={} retryable={} correlationId={} message={} context={}";

        if (type.getHttpStatus() >= 500) {
            log.error(pattern,
                    category,
                    type.getCode(),
                    type.getHttpStatus(),
                    type.isRetryable(),
                    correlationId,
                    detail,
                    properties,
                    ex);
        } else {
            log.warn(pattern,
                    category,
                    type.getCode(),
                    type.getHttpStatus(),
                    type.isRetryable(),
                    correlationId,
                    properties,
                    detail);
        }
    }

}