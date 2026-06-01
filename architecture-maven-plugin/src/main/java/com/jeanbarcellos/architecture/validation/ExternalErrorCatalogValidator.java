package com.jeanbarcellos.architecture.validation;

import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.core.error.ExternalErrorType;
import com.jeanbarcellos.architecture.scanner.ClassScanner;
import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.rule.ErrorCatalogNotEmptyRule;
import com.jeanbarcellos.architecture.validation.rule.ExternalErrorCodeRule;
import com.jeanbarcellos.architecture.validation.rule.ExternalErrorMappingRule;
import com.jeanbarcellos.architecture.validation.rule.ExternalErrorTitleRule;
import com.jeanbarcellos.architecture.validation.rule.ExternalErrorTypeMustBeEnumRule;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;

/**
 * Executor das validações dos catálogos
 * de erros externos.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ExternalErrorCatalogValidator {

    private final ClassLoader classLoader;

    public ExternalErrorCatalogValidator(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void validate(ValidationContext context) throws MojoExecutionException {

        Set<Class<? extends ExternalErrorType>> catalogs = ClassScanner.findImplementations(
                ExternalErrorType.class,
                classLoader);

        ValidationRule<Class<?>> enumRule = new ExternalErrorTypeMustBeEnumRule();

        ValidationRule<Class<?>> notEmptyRule = new ErrorCatalogNotEmptyRule();

        List<ValidationRule<ExternalErrorType>> itemRules = List.of(
                new ExternalErrorCodeRule(),
                new ExternalErrorTitleRule(),
                new ExternalErrorMappingRule());

        for (Class<? extends ExternalErrorType> catalog : catalogs) {

            enumRule.validate(catalog, context);
            notEmptyRule.validate(catalog, context);

            for (Object constant : catalog.getEnumConstants()) {

                ExternalErrorType externalError = (ExternalErrorType) constant;

                for (ValidationRule<ExternalErrorType> rule : itemRules) {
                    rule.validate(externalError, context);
                }
            }
        }
    }
}