package com.jeanbarcellos.architecture.validation;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.jeanbarcellos.architecture.scanner.ProjectClassLoaderFactory;
import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.error.validator.ErrorCatalogValidator;
import com.jeanbarcellos.architecture.validation.external.validator.ExternalErrorCatalogValidator;

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
     * @throws MojoExecutionException quando alguma
     *                                validação falha
     */
    public void validate() throws MojoExecutionException {

        ClassLoader classLoader = ProjectClassLoaderFactory.create(project);

        ValidationContext context = new ValidationContext();

        new ErrorCatalogValidator(classLoader).validate(context);
        new ExternalErrorCatalogValidator(classLoader).validate(context);
    }

}