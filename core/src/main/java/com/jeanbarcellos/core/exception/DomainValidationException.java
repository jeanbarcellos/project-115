package com.jeanbarcellos.core.exception;

import java.util.Collections;
import java.util.List;

import com.jeanbarcellos.core.error.DomainViolation;

/**
 * Exceção utilizada para representar múltiplas
 * violações de regras ou invariantes de domínio.
 *
 * <p>
 * Diferentemente de {@link ValidationException},
 * esta exceção representa inconsistências detectadas
 * dentro do modelo de domínio e não falhas de
 * validação de entrada.
 * </p>
 *
 * <p>
 * Esta exceção não possui qualquer dependência
 * de transporte (HTTP, REST, gRPC, mensageria, etc).
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@SuppressWarnings({ "java:S110", "java:S1948" })
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