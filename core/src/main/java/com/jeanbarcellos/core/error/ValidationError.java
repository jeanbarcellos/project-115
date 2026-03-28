package com.jeanbarcellos.core.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationError {
    private String field;
    private String message;
    private Object rejectedValue;
}