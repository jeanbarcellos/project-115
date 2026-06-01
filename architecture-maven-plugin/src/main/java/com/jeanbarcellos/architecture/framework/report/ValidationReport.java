package com.jeanbarcellos.architecture.framework.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jeanbarcellos.architecture.framework.rule.ValidationRule;

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
     * Gera representação textual do relatório.
     *
     * @return relatório formatado
     */
    public String format() {

        StringBuilder builder = new StringBuilder();

        builder.append(System.lineSeparator());

        builder.append(
                "==================================================")
                .append(System.lineSeparator());

        builder.append(
                " Architecture Validation Report")
                .append(System.lineSeparator());

        builder.append(
                "==================================================")
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        for (ValidationViolation violation : violations) {

            builder.append("[")
                    .append(violation.getModule().name())
                    .append("/")
                    .append(violation.getCategory().name())
                    .append("]")
                    .append(System.lineSeparator());

            builder.append("Rule: ")
                    .append(violation.getRule().metadata().getCode())
                    .append(" - ")
                    .append(violation.getRule().metadata().getName());

            builder.append("Class: ")
                    .append(violation.getClassName())
                    .append(System.lineSeparator());

            if (violation.getFilePath() != null) {
                builder.append("File : ")
                        .append(violation.getFilePath())
                        .append(System.lineSeparator());
            }

            if (violation.getElement() != null) {
                builder.append("Element : ")
                        .append(violation.getElement())
                        .append(System.lineSeparator());
            }

            builder.append("Description:")
                    .append(System.lineSeparator())
                    .append(violation.getRule().metadata().getDescription());

            builder.append("Recommendation:")
                    .append(System.lineSeparator())
                    .append(violation.getRule().metadata().getRecommendation());

            builder.append("Message: ")
                    .append(violation.getMessage())
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
        }

        return builder.toString();
    }

}