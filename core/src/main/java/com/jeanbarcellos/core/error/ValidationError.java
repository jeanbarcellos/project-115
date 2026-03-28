package com.jeanbarcellos.core.error;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true) // retira o prefixo getsset
public class ValidationError {
    private String field;
    private String message;
    private Object rejectedValue;
}