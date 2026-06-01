package com.jeanbarcellos.architecture.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.jeanbarcellos.architecture.validation.ArchitectureValidator;

/**
 * Goal principal do plugin responsável por executar
 * todas as validações arquiteturais da plataforma.
 *
 * <p>
 * Este goal é executado durante a fase
 * {@code verify} do ciclo de vida Maven.
 * </p>
 *
 * <p>
 * O objetivo é impedir que violações
 * arquiteturais cheguem aos ambientes
 * de homologação ou produção.
 * </p>
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

    /**
     * Executa todas as validações arquiteturais.
     *
     * @throws MojoExecutionException quando alguma regra arquitetural é violada
     */
    @Override
    public void execute() throws MojoExecutionException {

        this.getLog().info("");
        this.getLog().info("========================================");
        this.getLog().info(" Project115 Architecture Validation");
        this.getLog().info("========================================");

        new ArchitectureValidator(project).validate();

        this.getLog().info("Architecture validation completed.");
    }

}