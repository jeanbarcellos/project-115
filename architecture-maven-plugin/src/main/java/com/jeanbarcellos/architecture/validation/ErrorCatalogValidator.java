package com.jeanbarcellos.architecture.validation;

import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.scanner.ClassScanner;
import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.rule.ErrorCatalogNotEmptyRule;
import com.jeanbarcellos.architecture.validation.rule.ErrorCodeFormatRule;
import com.jeanbarcellos.architecture.validation.rule.ErrorCodeUniqueRule;
import com.jeanbarcellos.architecture.validation.rule.ErrorHttpStatusRule;
import com.jeanbarcellos.architecture.validation.rule.ErrorTitleRule;
import com.jeanbarcellos.architecture.validation.rule.ErrorTypeMustBeEnumRule;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;
import com.jeanbarcellos.core.error.ErrorType;

/**
 * Executor das validações dos catálogos de erro internos.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorCatalogValidator {

    private final ClassLoader classLoader;

    public ErrorCatalogValidator(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void validate(ValidationContext context) throws MojoExecutionException {

        Set<Class<? extends ErrorType>> catalogs = ClassScanner.findImplementations(ErrorType.class, classLoader);

        ValidationRule<Class<?>> enumRule = new ErrorTypeMustBeEnumRule();
        ValidationRule<Class<?>> notEmptyRule = new ErrorCatalogNotEmptyRule();

        List<ValidationRule<ErrorType>> itemRules = List.of(
                new ErrorCodeUniqueRule(),
                new ErrorCodeFormatRule(),
                new ErrorTitleRule(),
                new ErrorHttpStatusRule());

        for (Class<? extends ErrorType> catalog : catalogs) {

            enumRule.validate(catalog, context);
            notEmptyRule.validate(catalog, context);

            for (Object constant : catalog.getEnumConstants()) {

                ErrorType errorType = (ErrorType) constant;

                for (ValidationRule<ErrorType> rule : itemRules) {
                    rule.validate(errorType, context);
                }
            }
        }
    }
}