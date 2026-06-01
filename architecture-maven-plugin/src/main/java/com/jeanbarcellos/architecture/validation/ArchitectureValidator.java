package com.jeanbarcellos.architecture.validation;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.jeanbarcellos.architecture.scanner.ProjectClassLoaderFactory;
import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.error.validator.ErrorCatalogValidator;
import com.jeanbarcellos.architecture.validation.external.validator.ExternalErrorCatalogValidator;
import com.jeanbarcellos.architecture.validation.report.ValidationCategory;
import com.jeanbarcellos.architecture.validation.report.ValidationException;
import com.jeanbarcellos.architecture.validation.report.ValidationReport;
import com.jeanbarcellos.architecture.validation.report.ValidationViolation;

/**
 * Orquestrador central responsável por executar
 * todos os validadores arquiteturais registrados.
 *
 * <p>
 * Novos validadores devem ser adicionados aqui
 * para participarem automaticamente do pipeline
 * de validação.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ArchitectureValidator {

    /**
     * Projeto Maven atual.
     */
    private final MavenProject project;

    /**
     * Cria uma nova instância.
     *
     * @param project projeto Maven atual
     */
    public ArchitectureValidator(MavenProject project) {
        this.project = project;
    }

    /**
     * Executa todas as validações arquiteturais.
     *
     * @throws MojoExecutionException quando alguma validação falha
     */
    public void validate() throws MojoExecutionException {

        ClassLoader classLoader = ProjectClassLoaderFactory.create(project);

        ValidationContext context = new ValidationContext();

        new ErrorCatalogValidator(classLoader).validate(context);
        new ExternalErrorCatalogValidator(classLoader).validate(context);
    }

    private void validateReport(ValidationContext context) throws ValidationException {

        ValidationReport report = context.getReport();

        if (!report.hasViolations()) {
            return;
        }

        StringBuilder builder = new StringBuilder();

        builder.append(System.lineSeparator());
        builder.append("==================================================")
                .append(System.lineSeparator());

        builder.append(" Architecture Validation Report")
                .append(System.lineSeparator());

        builder.append("==================================================")
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        for (ValidationCategory category : ValidationCategory.values()) {

            boolean categoryPrinted = false;

            for (ValidationViolation violation : report.getViolations()) {

                if (violation.getCategory() != category) {
                    continue;
                }

                if (!categoryPrinted) {

                    builder.append("[")
                            .append(category.getCode())
                            .append("]")
                            .append(System.lineSeparator());

                    categoryPrinted = true;
                }

                builder.append(" - ")
                        .append(violation.getMessage())
                        .append(System.lineSeparator());
            }

            if (categoryPrinted) {
                builder.append(System.lineSeparator());
            }
        }

        throw new ValidationException(builder.toString());
    }

}