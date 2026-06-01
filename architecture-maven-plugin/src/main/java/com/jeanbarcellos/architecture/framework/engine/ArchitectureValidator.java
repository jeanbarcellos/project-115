package com.jeanbarcellos.architecture.framework.engine;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.registry.ValidationModules;
import com.jeanbarcellos.architecture.framework.report.ValidationException;
import com.jeanbarcellos.architecture.framework.validator.ValidatorModule;
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

        List<ValidatorModule> modules = ValidationModules.all();

        for (ValidatorModule module : modules) {
            module.validate(context);
        }

        if (context.getReport().hasViolations()) {
            throw new ValidationException(context.getReport());
        }
    }

}