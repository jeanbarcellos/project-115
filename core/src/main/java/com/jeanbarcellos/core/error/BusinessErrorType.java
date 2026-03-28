package com.jeanbarcellos.core.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true) // retira o prefixo getsset
public enum BusinessErrorType implements ApiErrorType {

    VALIDATION_ERROR("validation-error", 422, "Validation failed");

    private final String code;
    private final int httpStatus;
    private final String title;

}
