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
import com.jeanbarcellos.core.exception.integration.IntegrationException;
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
    public ResponseEntity<ErrorResponse> handleDomainException(
            DomainException ex,
            HttpServletRequest request) {

        // ⚠️ fallback global (sem contexto específico de módulo)
        ErrorType errorType = TechnicalErrorType.SYSTEM_VALIDATION_ERROR;

        // Context do dominio não deveria ser repassado para client
        Map<String, Object> properties = ObjectUtils.isNotEmpty(ex.getContext()) ? ex.getContext(): null;

        ErrorResponse errorResposne = ErrorResponse.builder()
                .type(resolveTypeUri(errorType))
                .title(errorType.getTitle())
                .status(errorType.getHttpStatus())
                .detail(ex.getMessage()) // Mensagem customizada da exception
                .instance(resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .properties(properties) // Propriedades extras/contextos
                .build();

        this.log("domain", errorType, ex, ex.getMessage());

        return ResponseEntity.status(errorType.getHttpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(errorResposne);
    }

    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<ErrorResponse> handleDomainValidation(
            DomainValidationException ex,
            HttpServletRequest request) {

        ErrorType errorType = TechnicalErrorType.INPUT_VALIDATION_ERROR;

        this.log("validation", errorType, ex, ex.getMessage());

        List<ValidationError> errors = ex.getViolations()
                .stream()
                .map(violation -> ValidationError.of(
                        violation.getField(),
                        violation.getMessage(),
                        violation.getRejectedValue()))
                .toList();

        ErrorResponse errorResposne = ErrorResponse.builder()
                .type(resolveTypeUri(errorType))
                .title(errorType.getTitle())
                .status(errorType.getHttpStatus())
                .detail(ex.getMessage()) // Mensagem customizada da exception
                .instance(resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .errors(errors) // Campo customizado de erros
                .build();

        return ResponseEntity.status(errorType.getHttpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(errorResposne);
    }

    // BUSINESS ===============================================================

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {

        ErrorType errorType = ex.getType();
        Map<String, Object> properties = ObjectUtils.isNotEmpty(ex.getProperties()) ? ex.getProperties(): null;

        this.log("business", errorType, ex, ex.getMessage());

        ErrorResponse errorResposne = ErrorResponse.builder()
                .type(resolveTypeUri(errorType))
                .title(errorType.getTitle())
                .status(errorType.getHttpStatus())
                .detail(ex.getMessage()) // Mensagem customizada da exception
                .instance(resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .properties(properties) // Propriedades extras/contextos
                .build();

        return ResponseEntity.status(errorType.getHttpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(errorResposne);
    }

    // VALIDATION → 422 =======================================================

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex,
            HttpServletRequest request) {

        TechnicalErrorType errorType = TechnicalErrorType.INPUT_VALIDATION_ERROR;
        List<ValidationError> errors = ex.getErrors();

        this.log("validation", errorType, ex, ex.getMessage());

        ErrorResponse errorResposne = ErrorResponse.builder()
                .type(resolveTypeUri(errorType))
                .title(errorType.getTitle())
                .status(errorType.getHttpStatus())
                .detail(ex.getMessage()) // Mensagem customizada da exception
                .instance(resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .errors(errors) // Campo customizado de erros
                .build();

        return ResponseEntity.status(errorType.getHttpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(errorResposne);
    }

    // INTEGRATION ============================================================

    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrationException(IntegrationException ex, HttpServletRequest request) {
        this.log(
                "integration",
                ex.getErrorType(),
                ex,
                ex.getMessage());

        return this.buildResponse(
                request,
                ex.getErrorType(),
                ex.getMessage(),
                null,
                null);
    }

    // APPLICATION (fallback controlado) ======================================

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(
            ApplicationException ex,
            HttpServletRequest request) {

        // Sem tipo explícito → vira erro interno
        TechnicalErrorType errorType = TechnicalErrorType.INTERNAL_ERROR;

        this.log("technical", errorType, ex, ex.getMessage());

        ErrorResponse errorResposne = ErrorResponse.builder()
                .type(resolveTypeUri(errorType))
                .title(errorType.getTitle())
                .status(errorType.getHttpStatus())
                .detail(ex.getMessage()) // Mensagem customizada da exception
                .instance(resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .build();

        return ResponseEntity.status(errorType.getHttpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(errorResposne);
    }

    // GENERIC / TECHNICAL ====================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleTechnicalException(
            Exception ex,
            HttpServletRequest request) {

        TechnicalErrorType errorType = TechnicalErrorResolver.resolveType(ex);
        String detail = "Unexpected error";

        this.log("technical", errorType, ex, detail);

        ErrorResponse errorResposne = ErrorResponse.builder()
                .type(resolveTypeUri(errorType))
                .title(errorType.getTitle())
                .status(errorType.getHttpStatus())
                .detail(detail)
                .instance(this.resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId())
                .properties(Map.of("retryable", errorType.isRetryable()))
                .build();

        return ResponseEntity.status(errorType.getHttpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(errorResposne);
    }


    // RESOLVERS ==============================================================

    private URI resolveTypeUri(ErrorType errorType) {
        return URI.create(this.problemBaseUri + "/" + errorType.getCode());
    }

    private URI resolveInstance(HttpServletRequest request) {
        return URI.create(request.getRequestURI());
    }

    private String getCorrelationId() {
        String correlationId = CorrelationContext.get();
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = "no-correlation-id"; // Ou generate um UUID temporário
        }

        return correlationId;
    }

    // BUILDERS ===============================================================

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpServletRequest request,
            ErrorType errorType,
            String detail,
            List<ValidationError> errors,
            Map<String, Object> properties) {

        var responseBuilder = ErrorResponse.builder()
                .type(resolveTypeUri(errorType))
                .title(errorType.getTitle())
                .status(errorType.getHttpStatus())
                .detail(detail)
                .instance(this.resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(this.getCorrelationId());

        // 3. Verificação de Erros de Validação (Só adiciona se houver conteúdo)
        if (ObjectUtils.isNotEmpty(errors)) {
            responseBuilder.errors(errors);
        }

        // 4. Verificação e Merge de Properties
        Map<String, Object> finalProperties = this.buildProperties(errorType, properties);

        if (ObjectUtils.isNotEmpty(finalProperties.isEmpty())) {
            responseBuilder.properties(finalProperties);
        }

        ErrorResponse body = responseBuilder.build();

        return ResponseEntity.status(errorType.getHttpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(body);
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

    // LOGGING ================================================================

    private void log(String category, ErrorType errorType, Exception ex, String detail) {

        String correlationId = this.getCorrelationId();

        // Erros não técnicos não é necessário logar
        String pattern = "[error][{}] code={} status={} retryable={} correlationId={} message={}";

        if (errorType.getHttpStatus() >= 500) {
            log.error(pattern,
                    category,
                    errorType.getCode(),
                    errorType.getHttpStatus(),
                    errorType.isRetryable(),
                    correlationId,
                    detail,
                    ex);
        } else {
            log.warn(pattern,
                    category,
                    errorType.getCode(),
                    errorType.getHttpStatus(),
                    errorType.isRetryable(),
                    correlationId,
                    detail);
        }
    }

}