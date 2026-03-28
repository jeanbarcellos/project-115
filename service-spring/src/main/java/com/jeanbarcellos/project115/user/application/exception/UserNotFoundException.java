package com.jeanbarcellos.project115.user.application.exception;

import java.util.Map;

import com.jeanbarcellos.core.exception.DomainException;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException(Long id) {
        super("User not found", Map.of("userId", id));
    }
}