package com.jeanbarcellos.architecture.framework.report.formatter;

import com.jeanbarcellos.architecture.framework.report.ValidationReport;

/**
 * Contrato para formatação de relatórios
 * de validação arquitetural.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public interface ValidationReportFormatter {

    /**
     * Formata relatório.
     *
     * @param report relatório
     *
     * @return conteúdo formatado
     */
    String format(ValidationReport report);

}