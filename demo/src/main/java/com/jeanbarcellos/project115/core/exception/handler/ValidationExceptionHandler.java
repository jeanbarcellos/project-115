package com.jeanbarcellos.project115.core.exception.handler;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.jboss.logging.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setType(URI.create("https://api.exemplo.com/problems/validation-error"));
        problem.setTitle("Validation failed");
        problem.setDetail("One or more fields are invalid");
        problem.setInstance(URI.create(request.getRequestURI()));

        problem.setProperty("correlationId", MDC.get("correlationId"));

        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> Map.of(
                        "field", err.getField(),
                        "message", err.getDefaultMessage()))
                .toList();

        problem.setProperty("errors", errors);

        return ResponseEntity.badRequest().body(problem);
    }
}