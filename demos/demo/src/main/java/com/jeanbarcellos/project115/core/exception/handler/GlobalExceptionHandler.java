package com.jeanbarcellos.project115.core.exception.handler;

import static com.jeanbarcellos.project115.core.Constants.CORRELATION_ID_KEY;

import java.net.URI;

import org.slf4j.MDC;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jeanbarcellos.project115.core.error.BusinessErrorType;
import com.jeanbarcellos.project115.core.error.TechnicalErrorType;
import com.jeanbarcellos.project115.core.exception.DomainException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomain(
            DomainException ex,
            HttpServletRequest request) {
        BusinessErrorType type = ex.getErrorType();

        ProblemDetail problem = ProblemDetail.forStatus(type.status());
        problem.setType(type.type());
        problem.setTitle(type.title());
        problem.setDetail(ex.getMessage());
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty(CORRELATION_ID_KEY, MDC.get(CORRELATION_ID_KEY));

        return ResponseEntity.status(type.status()).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleTechnical(
            Exception ex,
            HttpServletRequest request) {
        TechnicalErrorType type = TechnicalErrorType.INTERNAL_ERROR;

        ProblemDetail problem = ProblemDetail.forStatus(type.status());
        problem.setType(type.type());
        problem.setTitle(type.title());
        problem.setDetail("Unexpected internal error");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty(CORRELATION_ID_KEY, MDC.get(CORRELATION_ID_KEY));

        return ResponseEntity.status(type.status()).body(problem);
    }
}