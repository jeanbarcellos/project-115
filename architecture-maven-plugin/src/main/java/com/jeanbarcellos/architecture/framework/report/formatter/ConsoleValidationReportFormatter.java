package com.jeanbarcellos.architecture.framework.report.formatter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jeanbarcellos.architecture.framework.report.ValidationModule;
import com.jeanbarcellos.architecture.framework.report.ValidationReport;
import com.jeanbarcellos.architecture.framework.report.ValidationViolation;
import com.jeanbarcellos.architecture.framework.rule.RuleMetadata;

/**
 * Responsável pela geração da representação
 * textual do relatório de validação.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public final class ConsoleValidationReportFormatter implements ValidationReportFormatter {

    /**
     * Formata relatório.
     *
     * @param report relatório
     *
     * @return representação textual
     */
    @Override
    public String format(ValidationReport report) {

        StringBuilder sb = new StringBuilder();

        appendHeader(sb);
        appendSummary(sb, report);

        Map<ValidationModule, List<ValidationViolation>> grouped = report.getViolations()
                .stream()
                .collect(Collectors.groupingBy(
                        ValidationViolation::getModule));

        for (ValidationModule module : ValidationModule.values()) {
            List<ValidationViolation> violations = grouped.get(module);

            if (violations == null || violations.isEmpty()) {
                continue;
            }

            appendModule(sb, module, violations);
        }

        return sb.toString();
    }

    private static void appendHeader(
            StringBuilder sb) {

        sb.append(System.lineSeparator());

        sb.append(
                "======================================================================")
                .append(System.lineSeparator());

        sb.append(
                " Architecture Validation Report")
                .append(System.lineSeparator());

        sb.append(
                "======================================================================")
                .append(System.lineSeparator());

        sb.append(System.lineSeparator());
    }

    private static void appendSummary(
            StringBuilder sb,
            ValidationReport report) {

        sb.append("Summary")
                .append(System.lineSeparator());

        sb.append(
                "----------------------------------------------------------------------")
                .append(System.lineSeparator());

        sb.append("Violations : ")
                .append(report.count())
                .append(System.lineSeparator());

        sb.append(System.lineSeparator());

        Map<ValidationModule, Long> totals = report.getViolations()
                .stream()
                .collect(Collectors.groupingBy(
                        ValidationViolation::getModule,
                        Collectors.counting()));

        for (ValidationModule module : ValidationModule.values()) {
            sb.append(
                    String.format(
                            "%-15s : %d",
                            module.name(),
                            totals.getOrDefault(module, 0L)))
                    .append(System.lineSeparator());
        }

        sb.append(System.lineSeparator());
    }

    private static void appendModule(
            StringBuilder sb,
            ValidationModule module,
            List<ValidationViolation> violations) {

        sb.append(
                "======================================================================")
                .append(System.lineSeparator());

        sb.append(module.name())
                .append(System.lineSeparator());

        sb.append(
                "======================================================================")
                .append(System.lineSeparator());

        sb.append(System.lineSeparator());

        for (ValidationViolation violation : violations) {

            appendViolation(
                    sb,
                    violation);
        }
    }

    private static void appendViolation(
            StringBuilder sb,
            ValidationViolation violation) {

        RuleMetadata metadata = violation.getRule().metadata();

        sb.append("Rule")
                .append(System.lineSeparator());

        sb.append("  ")
                .append(metadata.getCode())
                .append(" - ")
                .append(metadata.getName())
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        sb.append("Description")
                .append(System.lineSeparator());

        sb.append("  ")
                .append(metadata.getDescription())
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        sb.append("Recommendation")
                .append(System.lineSeparator());

        sb.append("  ")
                .append(metadata.getRecommendation())
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        sb.append("Class")
                .append(System.lineSeparator());

        sb.append("  ")
                .append(violation.getClassName())
                .append(System.lineSeparator());

        if (violation.getElement() != null) {

            sb.append(System.lineSeparator());

            sb.append("Element")
                    .append(System.lineSeparator());

            sb.append("  ")
                    .append(violation.getElement())
                    .append(System.lineSeparator());
        }

        sb.append(System.lineSeparator());

        sb.append("Message")
                .append(System.lineSeparator());

        sb.append("  ")
                .append(violation.getMessage())
                .append(System.lineSeparator());

        sb.append(System.lineSeparator());
    }

}