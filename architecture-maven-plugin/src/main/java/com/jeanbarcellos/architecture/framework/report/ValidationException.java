package com.jeanbarcellos.architecture.framework.report;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Exceção lançada ao final da execução
 * quando violações arquiteturais são encontradas.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ValidationException extends MojoExecutionException {

    /**
     * Cria uma nova exceção.
     *
     * @param message relatório formatado
     */
    public ValidationException(String message) {
        super(message);
    }

}