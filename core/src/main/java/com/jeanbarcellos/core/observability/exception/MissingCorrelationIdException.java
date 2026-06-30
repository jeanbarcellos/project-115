package com.jeanbarcellos.core.observability.exception;

public class MissingCorrelationIdException extends RuntimeException {

    public MissingCorrelationIdException() {
        super("Correlation ID is required");
    }

}