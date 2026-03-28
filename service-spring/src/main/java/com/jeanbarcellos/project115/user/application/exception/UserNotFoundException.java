package com.jeanbarcellos.project115.user.application.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String string) {
        super(string);
    }

}
