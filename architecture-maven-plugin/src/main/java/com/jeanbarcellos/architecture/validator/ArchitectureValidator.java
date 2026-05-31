package com.jeanbarcellos.architecture.validator;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.jeanbarcellos.architecture.scanner.ProjectClassLoaderFactory;

/**
 * Executor central das validações arquiteturais.
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

        new ErrorCatalogValidator(classLoader).validate();
        new ExternalErrorCatalogValidator(classLoader).validate();
    }
}