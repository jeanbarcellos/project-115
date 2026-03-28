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
import com.jeanbarcellos.core.error.BusinessErrorType;
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
        // .body(SpringProblemDetailMapper.toProblemDetail(apiError));
    }
}
