package com.jeanbarcellos.architecture.framework.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jeanbarcellos.architecture.framework.report.formatter.ConsoleValidationReportFormatter;
import com.jeanbarcellos.architecture.framework.rule.ValidationRule;
import com.jeanbarcellos.architecture.framework.rule.ValidationSeverity;

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
     * Registra uma nova violação.
     *
     * @param module       módulo responsável
     * @param category     categoria da validação
     * @param rule         regra que falhou
     * @param className    classe relacionada
     * @param element      elemento relacionado
     * @param filePath     arquivo relacionado
     * @param lineNumber   linha futura
     * @param columnNumber coluna futura
     * @param message      descrição da falha
     */
    public void addViolation(
            ValidationModule module,
            ValidationCategory category,
            ValidationRule rule,
            String className,
            String element,
            String filePath,
            Integer lineNumber,
            Integer columnNumber,
            String message) {

        this.violations.add(
                ValidationViolation.builder()
                        .module(module)
                        .category(category)
                        .rule(rule)
                        .className(className)
                        .element(element)
                        .filePath(filePath)
                        .lineNumber(lineNumber)
                        .columnNumber(columnNumber)
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

    /**
     * Indica se existem erros críticos.
     *
     * @return true quando houver erros
     */
    public boolean hasErrors() {
        return violations.stream()
                .anyMatch(v -> v.getRule().metadata()
                        .getSeverity() == ValidationSeverity.ERROR);
    }

    /**
     * Indica se existem warnings.
     *
     * @return true quando houver warnings
     */
    public boolean hasWarnings() {
        return violations.stream()
                .anyMatch(v -> v.getRule().metadata()
                        .getSeverity() == ValidationSeverity.WARNING);
    }

    /**
     * Quantidade total de violações.
     */
    public int count() {
        return violations.size();
    }

    /**
     * Gera relatório textual.
     */
    public String format() {
        return new ConsoleValidationReportFormatter().format(this);
    }

}