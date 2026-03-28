package com.jeanbarcellos.project115.user.adapter.api.error;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jeanbarcellos.core.error.ApiError;
import com.jeanbarcellos.core.error.ApiErrorType;
import com.jeanbarcellos.core.error.BusinessErrorType;
import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.ValidationError;
import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.core.observability.CorrelationContext;
import com.jeanbarcellos.project115.user.application.mapper.DomainToBusinessMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomain(DomainException ex, HttpServletRequest req) {

        BusinessException business = DomainToBusinessMapper.map(ex);

        return handleBusiness(business, req);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex, HttpServletRequest req) {

        ApiError error = buildError(
                ex.getType(),
                ex.getMessage(),
                ex.getProperties(),
                req.getRequestURI());

        return ResponseEntity.status(ex.getType().httpStatus())
                .contentType(MediaType.valueOf("application/problem+json"))
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {

        List<ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(field -> new ValidationError(
                        field.getField(),
                        field.getDefaultMessage(),
                        field.getRejectedValue()))
                .toList();

        BusinessErrorType type = BusinessErrorType.VALIDATION_ERROR;

        ApiError error = ApiError.builder()
                .type(type.type())
                .title(type.title())
                .status(type.httpStatus())
                .detail("One or more fields are invalid")
                .errors(validationErrors)
                .correlationId(CorrelationContext.get())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(type.httpStatus())
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleTechnical(Exception ex, HttpServletRequest req) {
        log.error("error", ex);

        TechnicalErrorType type = TechnicalErrorResolver.resolve(ex);

        ApiError error = buildError(
                type,
                "An unexpected error occurred",
                null,
                req.getRequestURI());

        return ResponseEntity.status(type.httpStatus())
                .contentType(MediaType.valueOf("application/problem+json"))
                .body(error);
    }

    private ApiError buildError(
            ApiErrorType type,
            String detail,
            Map<String, Object> properties,
            String uri) {
        return ApiError.builder()
                .type(type.type())
                .title(type.title())
                .status(type.httpStatus())
                .detail(detail)
                .instance(URI.create(uri))
                .timestamp(Instant.now())
                .correlationId(CorrelationContext.get())
                .properties(properties)
                .build();
    }
}