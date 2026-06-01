package com.jeanbarcellos.architecture.validation.context;

import java.util.HashSet;
import java.util.Set;

import com.jeanbarcellos.architecture.validation.report.ValidationCategory;
import com.jeanbarcellos.architecture.validation.report.ValidationReport;

import lombok.Getter;

/**
 * Contexto compartilhado entre todas as regras
 * executadas durante o processo de validação.
 *
 * <p>
 * Permite compartilhar informações entre
 * diferentes validadores sem acoplamento direto.
 * </p>
 *
 * <p>
 * Atualmente é utilizado para controle
 * de unicidade global dos códigos de erro.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
public class ValidationContext {

    /**
     * Controle de unicidade global dos códigos.
     */
    private final Set<String> errorCodes = new HashSet<>();

    /**
     * Relatório acumulado.
     */
    private final ValidationReport report = new ValidationReport();

    /**
     * Registra uma nova violação.
     *
     * @param category categoria da validação
     * @param message  descrição da falha
     */
    public void addViolation(ValidationCategory category, String message) {
        this.report.addViolation(category, message);
    }
}