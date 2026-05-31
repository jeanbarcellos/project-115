package com.jeanbarcellos.architecture.validator;

import org.apache.maven.plugin.MojoExecutionException;

public class ExternalErrorCatalogValidator {

    private final ClassLoader classLoader;

    public ExternalErrorCatalogValidator(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void validate() throws MojoExecutionException {
    }

}
