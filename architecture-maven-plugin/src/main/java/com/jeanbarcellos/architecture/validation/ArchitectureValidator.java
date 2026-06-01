package com.jeanbarcellos.architecture.validation;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.jeanbarcellos.architecture.scanner.ProjectClassLoaderFactory;
import com.jeanbarcellos.architecture.validation.context.ValidationContext;

/**
 * Orquestrador principal das validações arquiteturais.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ArchitectureValidator {

    private final MavenProject project;

    public ArchitectureValidator(MavenProject project) {
        this.project = project;
    }

    public void validate() throws MojoExecutionException {

        ClassLoader classLoader = ProjectClassLoaderFactory.create(project);

        ValidationContext context = new ValidationContext();

        new ErrorCatalogValidator(classLoader)
                .validate(context);

        new ExternalErrorCatalogValidator(classLoader)
                .validate(context);
    }
}