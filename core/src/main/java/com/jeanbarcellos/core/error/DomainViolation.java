package com.jeanbarcellos.core.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Representa uma violação de regra de domínio.
 */
@Getter
@AllArgsConstructor
public class DomainViolation {

    private String field;
    private String message;
    private Object rejectedValue;
}