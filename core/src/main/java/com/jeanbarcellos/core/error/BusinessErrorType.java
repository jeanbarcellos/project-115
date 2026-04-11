package com.jeanbarcellos.core.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

// TODO: TEMPORÁRIO
@Getter
@RequiredArgsConstructor
@Accessors(fluent = true) // retira o prefixo getsset
public enum BusinessErrorType implements ErrorType {

    // Domínio e negócio // Temporário
    DOMAIN_ERROR("domain-error", 400, "Domain error"),
    BUSINESS_ERROR("business-error", 422, "Business error"),
    VALIDATION_ERROR("validation-error", 422, "Validation failed");

    private final String code;
    private final int httpStatus;
    private final String title;

}
