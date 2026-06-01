package com.jeanbarcellos.architecture.validation.error.rule;

import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;
import com.jeanbarcellos.core.error.ErrorType;

/**
 * Garante utilização de kebab-case.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorCodeFormatRule
        implements ValidationRule<ErrorType> {

    private static final Pattern PATTERN = Pattern.compile("[a-z0-9]+(-[a-z0-9]+)*");

    @Override
    public void validate(
            ErrorType target,
            ValidationContext context)
            throws MojoExecutionException {

        String code = target.getCode();

        if (code == null || !PATTERN.matcher(code).matches()) {

            throw new MojoExecutionException(
                    "Invalid error code: "
                            + code);
        }
    }
}