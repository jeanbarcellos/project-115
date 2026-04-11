package com.jeanbarcellos.project115.user.adapter.api.error;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jeanbarcellos.core.error.BusinessErrorType;
import com.jeanbarcellos.core.error.ErrorResponse;
import com.jeanbarcellos.core.error.ErrorType;
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

    // DOMAIN → converte para BUSINESS ========================================

    // Errado *
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomain(
            DomainException ex,
            HttpServletRequest request) {

        ErrorType type = BusinessErrorType.DOMAIN_ERROR;

        ErrorResponse error = ErrorResponse.builder()
                .type(resolveTypeUri(type))
                .title(type.title())
                .status(type.httpStatus())
                .detail(ex.getMessage())
                .instance(resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(CorrelationContext.get())
                .properties(ex.getContext())
                .build();

        log.warn("[domain-error] code={} correlationId={}",
                type.code(),
                CorrelationContext.get());

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
                .correlationId(CorrelationContext.get())
                .properties(ex.getProperties()) // Propriedades extras/contextos
                .build();

        log.warn("[business-error] code={} correlationId={}",
                type.code(),
                CorrelationContext.get());

        return ResponseEntity.status(type.httpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(error);
    }

    // VALIDATION → 422 =======================================================

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex,
            HttpServletRequest request) {

        TechnicalErrorType type = TechnicalErrorType.VALIDATION_ERROR;

        ErrorResponse error = ErrorResponse.builder()
                .type(resolveTypeUri(type))
                .title(type.title())
                .status(type.httpStatus())
                .detail(ex.getMessage()) // Mensagem customizada
                .instance(resolveInstance(request))
                .timestamp(Instant.now())
                .correlationId(CorrelationContext.get())
                .errors(ex.getErrors()) // Campo customizado de erros
                .build();

        log.warn("[validation-error] correlationId={} errors={}",
                type.code(),
                CorrelationContext.get(),
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
                .correlationId(CorrelationContext.get())
                .build();

        log.error("[{}] correlationId={} message={}",
                type.code(),
                CorrelationContext.get(),
                ex.getMessage(),
                ex);

        return ResponseEntity.status(type.httpStatus())
                .contentType(MediaType.valueOf(MEDIA_TYPE_APPLICATION_PROBLEM_JSON))
                .body(error);
    }

    // TECHNICAL ==============================================================

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
                .instance(resolveInstance(request))
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

    // HELPERS ================================================================

    private URI resolveTypeUri(ErrorType type) {
        return URI.create(problemBaseUri + "/" + type.code());
    }

    private URI resolveInstance(HttpServletRequest request) {
        return URI.create(request.getRequestURI());
    }

}