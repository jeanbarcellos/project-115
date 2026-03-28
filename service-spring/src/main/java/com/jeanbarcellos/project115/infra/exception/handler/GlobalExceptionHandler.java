package com.jeanbarcellos.project115.infra.exception.handler;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jeanbarcellos.core.error.ApiError;
import com.jeanbarcellos.core.error.ApiErrorType;
import com.jeanbarcellos.core.error.ValidationError;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.core.observability.CorrelationContext;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {

        List<ValidationError> fields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(field -> new ValidationError(
                        field.getField(),
                        field.getDefaultMessage(),
                        field.getRejectedValue()))
                .toList();

        ApiError error = new ApiError(
                URI.create("https://api.exemplo.com/problems/v1/validation-error"),
                "Validation failed",
                400,
                "One or more fields are invalid",
                URI.create(req.getRequestURI()),
                CorrelationContext.get(),
                Instant.now(),
                Map.of("errors", fields));

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<?> handleDomain(DomainException ex, HttpServletRequest request) {

        ApiErrorType type = ex.getType();

        String correlationId = CorrelationContext.get();
        log.info("correlationId: {}", correlationId);

        ApiError apiError = new ApiError(
                type.type(),
                type.title(),
                type.httpStatus(),
                ex.getMessage(),
                URI.create(request.getRequestURI()),
                correlationId,
                Instant.now(),
                ex.getContext());

        return ResponseEntity
                .status(type.httpStatus())
                .body(apiError);
        // .body(SpringProblemDetailMapper.toProblemDetail(apiError));
    }
}
