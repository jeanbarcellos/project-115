package com.jeanbarcellos.architecture.validator;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.scanner.ClassScanner;
import com.jeanbarcellos.core.error.ErrorType;

/**
 * Valida implementações de ErrorType.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorCatalogValidator {

    private final ClassLoader classLoader;

    public ErrorCatalogValidator(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void validate() throws MojoExecutionException {

        Set<String> codes = new HashSet<>();

        for (Class<? extends ErrorType> clazz : ClassScanner.findImplementations(ErrorType.class, classLoader)) {

            validateEnum(clazz);

            for (Object constant : clazz.getEnumConstants()) {

                ErrorType error = (ErrorType) constant;

                validateCode(error, codes);

                validateTitle(error);

                validateHttpStatus(error);
            }
        }
    }

    private void validateEnum(Class<?> clazz)
            throws MojoExecutionException {

        if (!clazz.isEnum()) {
            throw new MojoExecutionException(
                    clazz.getName() + " must be an enum");
        }
    }

    private void validateCode(
            ErrorType error,
            Set<String> codes)
            throws MojoExecutionException {

        if (!codes.add(error.getCode())) {

            throw new MojoExecutionException(
                    "Duplicated error code: "
                            + error.getCode());
        }
    }

    private void validateTitle(ErrorType error)
            throws MojoExecutionException {

        if (error.getTitle() == null
                || error.getTitle().isBlank()) {

            throw new MojoExecutionException(
                    "Title cannot be empty: "
                            + error.getCode());
        }
    }

    private void validateHttpStatus(ErrorType error)
            throws MojoExecutionException {

        int status = error.getHttpStatus();

        if (status < 400 || status > 599) {

            throw new MojoExecutionException(
                    "Invalid status for error: "
                            + error.getCode());
        }
    }
}