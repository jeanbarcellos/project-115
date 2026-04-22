package com.jeanbarcellos.core.exception;

import java.util.Collections;
import java.util.List;

import com.jeanbarcellos.core.error.DomainViolation;

/**
 * Exceção para múltiplas violações de domínio.
 *
 * NÃO depende de HTTP ou RFC.
 */
public class DomainValidationException extends DomainException {

    private final List<DomainViolation> violations;

    public DomainValidationException(String message, List<DomainViolation> violations) {
        super(message);
        this.violations = violations != null ? violations : Collections.emptyList();
    }

    public List<DomainViolation> getViolations() {
        return violations;
    }
}