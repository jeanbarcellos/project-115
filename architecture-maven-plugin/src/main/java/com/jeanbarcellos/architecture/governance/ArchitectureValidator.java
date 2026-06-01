package com.jeanbarcellos.architecture.governance;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationException;
import com.jeanbarcellos.architecture.framework.validator.ValidatorModule;
import com.jeanbarcellos.architecture.governance.error.validator.ErrorCatalogValidator;
import com.jeanbarcellos.architecture.governance.external.validator.ExternalErrorCatalogValidator;
import com.jeanbarcellos.architecture.scanner.ProjectClassLoaderFactory;

/**
 * Orquestrador central responsável por executar
 * todos os validadores arquiteturais registrados.
 *
 * <p>
 * Responsável por:
 * </p>
 * <ul>
 * <li>criar o contexto compartilhado;</li>
 * <li>executar os validadores registrados;</li>
 * <li>gerar o relatório consolidado;</li>
 * <li>falhar o build quando necessário.</li>
 * </ul>
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

        ValidationContext context = new ValidationContext(project, classLoader);

        List<ValidatorModule> modules = List.of(
                new ErrorCatalogValidator(),
                new ExternalErrorCatalogValidator());

        for (ValidatorModule module : modules) {
            module.validate(context);
        }

        if (context.getReport().hasViolations()) {
            throw new ValidationException(context.getReport().format());
        }
    }

    // /**
    //  * Analisa o relatório final e interrompe
    //  * o build quando existirem violações.
    //  *
    //  * @param context contexto compartilhado
    //  *
    //  * @throws ValidationException quando houver violações
    //  */
    // private void validateReport(ValidationContext context) throws ValidationException {

    //     ValidationReport report = context.getReport();

    //     if (!report.hasViolations()) {
    //         return;
    //     }

    //     throw new ValidationException(
    //             this.buildReport(report));
    // }

    // /**
    //  * Constrói o relatório textual final.
    //  *
    //  * @param report relatório consolidado
    //  *
    //  * @return relatório formatado
    //  */
    // private String buildReport(ValidationReport report) {

    //     StringBuilder builder = new StringBuilder();

    //     builder.append(System.lineSeparator());

    //     builder.append(
    //             "==================================================")
    //             .append(System.lineSeparator());

    //     builder.append(
    //             " Architecture Validation Report")
    //             .append(System.lineSeparator());

    //     builder.append(
    //             "==================================================")
    //             .append(System.lineSeparator())
    //             .append(System.lineSeparator());

    //     Map<ValidationCategory, List<ValidationViolation>> grouped = this.groupByCategory(report);

    //     grouped.forEach((category, violations) -> {

    //         builder.append("[")
    //                 .append(category.getCode())
    //                 .append("]")
    //                 .append(System.lineSeparator());

    //         violations.forEach(violation -> {

    //             builder.append("Class : ")
    //                     .append(violation.getClassName())
    //                     .append(System.lineSeparator());

    //             builder.append("File  : ")
    //                     .append(violation.getFilePath())
    //                     .append(System.lineSeparator());

    //             builder.append("Error : ")
    //                     .append(violation.getMessage())
    //                     .append(System.lineSeparator())
    //                     .append(System.lineSeparator());
    //         });
    //     });

    //     return builder.toString();
    // }

    // /**
    //  * Agrupa violações por categoria.
    //  *
    //  * @param report relatório consolidado
    //  *
    //  * @return violações agrupadas
    //  */
    // private Map<ValidationCategory, List<ValidationViolation>> groupByCategory(ValidationReport report) {

    //     Map<ValidationCategory, List<ValidationViolation>> result = new EnumMap<>(ValidationCategory.class);

    //     for (ValidationCategory category : ValidationCategory.values()) {

    //         List<ValidationViolation> violations = report.getViolations()
    //                 .stream()
    //                 .filter(v -> v.getCategory() == category)
    //                 .toList();

    //         if (!violations.isEmpty()) {
    //             result.put(category, violations);
    //         }
    //     }

    //     return result;
    // }

}