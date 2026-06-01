package com.jeanbarcellos.architecture.framework.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.report.ValidationModule;

/**
 * Garante que um catálogo possua pelo menos
 * um item declarado.
 *
 * <p>
 * Esta regra evita catálogos vazios que
 * provavelmente representam implementações
 * incompletas ou esquecidas durante o desenvolvimento.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class EnumCatalogNotEmptyRule
        implements CatalogRule {

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleMetadata metadata() {
        return RuleMetadata.of("ERR-002",
                "Catálogo não pode estar vazio",
                "Todo catálogo baseado em enum deve possuir ao menos um item.",
                "Adicione pelo menos um elemento ao catálogo.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(Class<?> catalog, ValidationContext context) {

        Object[] constants = catalog.getEnumConstants();

        if (constants == null || constants.length == 0) {

            context.addViolation(
                    ValidationModule.ERROR,
                    ValidationCategory.CATALOG,
                    this,
                    catalog,
                    "Empty catalog detected: " + catalog.getName());
        }
    }

}