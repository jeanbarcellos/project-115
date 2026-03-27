package com.jeanbarcellos.project115.support.exception.handler;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jeanbarcellos.project115.user.exception.UserNotFoundException;

@RestControllerAdvice
public class ProblemDetailsHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(UserNotFoundException ex) {

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        //
        problem.setTitle("User not found");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("https://example.com/problems/user-not-found"));

        var requestId = UUID.randomUUID();
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("correlationId", requestId);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(problem);
    }
}