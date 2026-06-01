package com.jeanbarcellos.architecture.framework.report;

import lombok.Builder;
import lombok.Getter;

/**
 * Representa uma violação arquitetural identificada
 * durante o processo de validação.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@Builder
public class ValidationViolation {

    /**
     * Categoria da validação.
     */
    private final ValidationCategory category;

    /**
     * Mensagem detalhando a violação.
     */
    private final String message;

}