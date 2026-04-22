package com.jeanbarcellos.project115.user.application.error;

import com.jeanbarcellos.core.error.ErrorType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Erros de negócio específicos do domínio de usuário.
 */
@Getter
@RequiredArgsConstructor
@Accessors(fluent = true) // retira o prefixo getsset
public enum UserErrorType implements ErrorType {

    USER_NOT_FOUND("user-not-found", 404, "User not found", false),
    EMAIL_ALREADY_EXISTS("email-already-exists", 409, "Email already exists", false);

    private final String code;
    private final int httpStatus;
    private final String title;
    private final boolean isRetryable;

}