package com.jeanbarcellos.project115.infra.exception.handler;

import java.net.URI;
import java.util.Map;

import static com.jeanbarcellos.core.Constants.CORRELATION_ID_HEADER;
import static com.jeanbarcellos.core.Constants.CORRELATION_ID_KEY;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jeanbarcellos.core.error.ApiError;
import com.jeanbarcellos.core.error.ApiErrorType;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.core.observability.CorrelationContext;
import com.jeanbarcellos.project115.infra.adapter.SpringProblemDetailMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomain(
            DomainException ex,
            HttpServletRequest request) {

        ApiErrorType type = ex.getErrorType();

        String correlationId = CorrelationContext.get();
        // String correlationContext = ""
        log.info("correlationId: {}", correlationId);

        ApiError apiError = new ApiError(
                type.type(),
                type.title(),
                type.httpStatus(),
                ex.getMessage(),
                URI.create(request.getRequestURI()),
                Map.of(CORRELATION_ID_KEY, correlationId))
                ;

        return ResponseEntity
                .status(type.httpStatus())
                .body(SpringProblemDetailMapper.toProblemDetail(apiError));
    }
}
