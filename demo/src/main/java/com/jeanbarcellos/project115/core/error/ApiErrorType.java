package com.jeanbarcellos.project115.core.error;

import java.net.URI;

import org.springframework.http.HttpStatus;

public interface ApiErrorType {
    URI type();

    HttpStatus status();

    String title();
}