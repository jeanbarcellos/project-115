package com.jeanbarcellos.architecture.framework.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

/**
 * Relatório acumulador de violações arquiteturais.
 *
 * <p>
 * Todas as regras registram suas falhas neste objeto.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
public class ValidationReport {

    private final List<ValidationViolation> violations = new ArrayList<>();

    /**
     * Adiciona uma nova violação.
     *
     * @param category categoria da regra
     * @param message  descrição da falha
     */
    public void addViolation(ValidationCategory category, String message) {

        this.violations.add(
                ValidationViolation.builder()
                        .category(category)
                        .message(message)
                        .build());
    }

    /**
     * Indica se existem falhas.
     *
     * @return true quando houver falhas
     */
    public boolean hasViolations() {
        return !violations.isEmpty();
    }

    /**
     * Retorna lista imutável.
     *
     * @return violações registradas
     */
    public List<ValidationViolation> getViolations() {
        return Collections.unmodifiableList(violations);
    }

}