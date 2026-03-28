package com.jeanbarcellos.core.error;

import java.net.URI;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true) // retira o prefixo getsset
public enum BusinessErrorType implements ApiErrorType {

    USER_NOT_FOUND(
            "user-not-found",
            404,
            "User not found"),

    INSUFFICIENT_BALANCE(
            "insufficient-balance",
            409,
            "Insufficient balance");


    private final String code;
    private final URI type;
    private final int httpStatus;
    private final String title;

    BusinessErrorType(String code, int httpStatus, String title) {
        this.code = code;
        this.type = URI.create("https://api.exemplo.com/problems/v1/" + code);
        this.httpStatus = httpStatus;
        this.title = title;
    }

}
