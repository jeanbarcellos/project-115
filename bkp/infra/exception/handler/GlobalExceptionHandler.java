package com.jeanbarcellos.project115.infra.exception.handler;

import java.net.URI;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.core.JsonParseException;
import com.jeanbarcellos.core.error.ApiError;
import com.jeanbarcellos.core.error.ApiErrorType;
import com.jeanbarcellos.core.error.BusinessErrorType;
import com.jeanbarcellos.core.error.ValidationError;
import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.core.observability.CorrelationContext;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {

        List<ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(field -> new ValidationError(
                        field.getField(),
                        field.getDefaultMessage(),
                        field.getRejectedValue()))
                .toList();

        ApiError error = ApiError.builder()
                .type(BusinessErrorType.VALIDATION_ERROR.type())
                .title(BusinessErrorType.VALIDATION_ERROR.title())
                .status(400)
                .detail("One or more fields are invalid")
                .errors(validationErrors)
                .correlationId(CorrelationContext.get())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<?> handleDomain(DomainException ex, HttpServletRequest request) {

        ApiErrorType type = ex.getType();

        String correlationId = CorrelationContext.get();
        log.info("correlationId: {}", correlationId);

        ApiError apiError = ApiError.builder()
                .type(type.type())
                .title(type.title())
                .status(type.httpStatus())
                .detail(ex.getMessage())
                .instance(URI.create(request.getRequestURI()))
                .correlationId(correlationId)
                .timestamp(Instant.now())
                .properties(ex.getContext())
                .build();

        return ResponseEntity
                .status(type.httpStatus())
                .body(apiError);
    }


        // BusinessException business = DomainToBusinessMapper.map(ex);
        // return handleBusiness(business, req);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(
            BusinessException ex,
            HttpServletRequest req
    ) {
        ApiError error = ApiError.builder()
                .type(ex.getType().type())
                .title(ex.getType().title())
                .status(ex.getStatus())
                .detail(ex.getMessage())
                .instance(URI.create(req.getRequestURI()))
                .properties(ex.getProperties())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(ex.getStatus())
                .contentType(MediaType.valueOf("application/problem+json"))
                .body(error);
    }

    // ***********************

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleTechnical(
            Exception ex,
            HttpServletRequest request) {

        ApiError error = ApiError.builder()
                .type(URI.create("https://api.exemplo.com/problems/internal-error"))
                .title("Internal server error")
                .status(500)
                .detail("An unexpected error occurred")
                .instance(URI.create(request.getRequestURI()))
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(500)
                .contentType(MediaType.valueOf("application/problem+json"))
                .body(error);
    }

    // ***********************

    @ExceptionHandler({
            SQLException.class
    })
    public ResponseEntity<ApiError> handleDatabase(Exception ex, HttpServletRequest req) {

        return buildError(
                503,
                "database-error",
                "Database unavailable",
                "Database error occurred",
                req);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            JsonParseException.class
    })
    public ResponseEntity<ApiError> handleMalformed(Exception ex, HttpServletRequest req) {

        return buildError(
                400,
                "malformed-json",
                "Malformed JSON",
                "Request body is invalid",
                req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(Exception ex, HttpServletRequest req) {

        return buildError(
                400,
                "invalid-parameter",
                "Invalid parameter",
                "Parameter type is invalid",
                req);
    }

    private ResponseEntity<ApiError> buildError(
            int status,
            String code,
            String title,
            String detail,
            HttpServletRequest req) {
        ApiError error = ApiError.builder()
                .type(URI.create("https://api.exemplo.com/problems/" + code))
                .title(title)
                .status(status)
                .detail(detail)
                .instance(URI.create(req.getRequestURI()))
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(status)
                .contentType(MediaType.valueOf("application/problem+json"))
                .body(error);
    }
}
