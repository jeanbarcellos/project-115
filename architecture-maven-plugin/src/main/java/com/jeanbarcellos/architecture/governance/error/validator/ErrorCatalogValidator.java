package com.jeanbarcellos.architecture.governance.error.validator;

import java.util.List;

import com.jeanbarcellos.architecture.framework.rule.ErrorCatalogNotEmptyRule;
import com.jeanbarcellos.architecture.framework.rule.ValidationRule;
import com.jeanbarcellos.architecture.framework.validator.AbstractCatalogValidator;
import com.jeanbarcellos.architecture.governance.error.rule.ErrorCodeFormatRule;
import com.jeanbarcellos.architecture.governance.error.rule.ErrorCodeUniqueRule;
import com.jeanbarcellos.architecture.governance.error.rule.ErrorHttpStatusRule;
import com.jeanbarcellos.architecture.governance.error.rule.ErrorTitleRule;
import com.jeanbarcellos.architecture.governance.error.rule.ErrorTypeMustBeEnumRule;
import com.jeanbarcellos.core.error.ErrorType;

/**
 * Executor responsável pela validação
 * dos catálogos que implementam ErrorType.
 *
 * <p>
 * Centraliza todas as regras relacionadas
 * ao catálogo oficial de erros da plataforma.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorCatalogValidator
        extends AbstractCatalogValidator<ErrorType> {

    /**
     * Cria uma nova instância.
     *
     * @param classLoader class loader do projeto
     */
    public ErrorCatalogValidator(ClassLoader classLoader) {
        super(classLoader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<ErrorType> getContract() {
        return ErrorType.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ErrorType cast(Object constant) {
        return (ErrorType) constant;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<ValidationRule<Class<?>>> getCatalogRules() {

        return List.of(
                new ErrorTypeMustBeEnumRule(),
                new ErrorCatalogNotEmptyRule());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<ValidationRule<ErrorType>> getItemRules() {

        return List.of(
                new ErrorCodeUniqueRule(),
                new ErrorCodeFormatRule(),
                new ErrorTitleRule(),
                new ErrorHttpStatusRule());
    }

}