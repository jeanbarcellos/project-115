package com.jeanbarcellos.architecture.governance.external.validator;

import java.util.List;

import com.jeanbarcellos.architecture.framework.rule.ErrorCatalogNotEmptyRule;
import com.jeanbarcellos.architecture.framework.rule.ValidationRule;
import com.jeanbarcellos.architecture.framework.validator.AbstractCatalogValidator;
import com.jeanbarcellos.architecture.governance.external.rule.ExternalErrorCodeRule;
import com.jeanbarcellos.architecture.governance.external.rule.ExternalErrorDescriptionRule;
import com.jeanbarcellos.architecture.governance.external.rule.ExternalErrorMappingRule;
import com.jeanbarcellos.architecture.governance.external.rule.ExternalErrorStatusRule;
import com.jeanbarcellos.architecture.governance.external.rule.ExternalErrorTypeMustBeEnumRule;
import com.jeanbarcellos.core.error.ExternalErrorType;

/**
 * Executor responsável pela validação
 * dos catálogos que implementam
 * {@link ExternalErrorType}.
 *
 * <p>
 * Centraliza todas as regras relacionadas
 * aos erros retornados por providers externos.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ExternalErrorCatalogValidator
        extends AbstractCatalogValidator<ExternalErrorType> {

    /**
     * Cria uma nova instância.
     *
     * @param classLoader class loader do projeto
     */
    public ExternalErrorCatalogValidator(ClassLoader classLoader) {
        super(classLoader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<ExternalErrorType> getContract() {
        return ExternalErrorType.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ExternalErrorType cast(Object constant) {
        return (ExternalErrorType) constant;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<ValidationRule<Class<?>>> getCatalogRules() {

        return List.of(
                new ExternalErrorTypeMustBeEnumRule(),
                new ErrorCatalogNotEmptyRule());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<ValidationRule<ExternalErrorType>> getItemRules() {

        return List.of(
                new ExternalErrorCodeRule(),
                new ExternalErrorDescriptionRule(),
                new ExternalErrorStatusRule(),
                new ExternalErrorMappingRule());
    }

}