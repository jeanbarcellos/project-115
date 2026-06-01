package com.jeanbarcellos.architecture.framework.report;

/**
 * Exceção lançada quando são encontradas
 * violações arquiteturais que impedem a
 * continuidade do build.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@SuppressWarnings("java:S1948")
public class ValidationException extends RuntimeException {

    /**
     * Relatório de validação.
     */
    private final ValidationReport report;

    /**
     * Cria nova exceção.
     *
     * @param report relatório gerado
     */
    public ValidationException(ValidationReport report) {
        super(report.format());
        this.report = report;
    }

    /**
     * Retorna o relatório associado.
     *
     * @return relatório
     */
    public ValidationReport getReport() {
        return report;
    }

}