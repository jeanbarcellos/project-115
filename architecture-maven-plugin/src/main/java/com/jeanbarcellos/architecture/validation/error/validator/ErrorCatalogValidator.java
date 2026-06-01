package com.jeanbarcellos.architecture.validation.error.validator;

import java.util.List;

import com.jeanbarcellos.core.error.ErrorType;
import com.jeanbarcellos.architecture.validation.error.rule.ErrorCodeFormatRule;
import com.jeanbarcellos.architecture.validation.error.rule.ErrorCodeUniqueRule;
import com.jeanbarcellos.architecture.validation.error.rule.ErrorHttpStatusRule;
import com.jeanbarcellos.architecture.validation.error.rule.ErrorTitleRule;
import com.jeanbarcellos.architecture.validation.error.rule.ErrorTypeMustBeEnumRule;
import com.jeanbarcellos.architecture.validation.rule.ErrorCatalogNotEmptyRule;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;
import com.jeanbarcellos.architecture.validation.validator.AbstractCatalogValidator;

/**
 * Validador dos catálogos ErrorType.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorCatalogValidator
        extends AbstractCatalogValidator<ErrorType> {

    public ErrorCatalogValidator(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    protected Class<ErrorType> getContract() {
        return ErrorType.class;
    }

    @Override
    protected ErrorType cast(Object constant) {
        return (ErrorType) constant;
    }

    @Override
    protected List<ValidationRule<Class<?>>> getCatalogRules() {

        return List.of(
                new ErrorTypeMustBeEnumRule(),
                new ErrorCatalogNotEmptyRule());
    }

    @Override
    protected List<ValidationRule<ErrorType>> getItemRules() {

        return List.of(
                new ErrorCodeUniqueRule(),
                new ErrorCodeFormatRule(),
                new ErrorTitleRule(),
                new ErrorHttpStatusRule());
    }
}