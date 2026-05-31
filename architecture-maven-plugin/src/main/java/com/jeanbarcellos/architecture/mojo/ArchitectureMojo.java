package com.jeanbarcellos.architecture.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.jeanbarcellos.architecture.validator.ArchitectureValidator;

/**
 * Executa as validações arquiteturais da plataforma.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Mojo(name = "validate", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class ArchitectureMojo extends AbstractMojo {

    /**
     * Projeto Maven atualmente em execução.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {

        getLog().info("");
        getLog().info("======================================");
        getLog().info(" Architecture Validation");
        getLog().info("======================================");

        ArchitectureValidator validator = new ArchitectureValidator(this.project);

        validator.validate();

        getLog().info("Architecture validation completed.");
    }
}