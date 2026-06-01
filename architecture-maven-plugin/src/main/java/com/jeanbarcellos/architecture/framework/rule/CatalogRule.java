package com.jeanbarcellos.architecture.framework.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;

/**
 * Regra aplicada ao catálogo.
 *
 * <p>
 * Executada uma única vez para cada
 * implementação encontrada pelo scanner.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public interface CatalogRule extends ValidationRule {

    /**
     * Executa a validação.
     *
     * @param catalog catálogo localizado
     * @param context contexto compartilhado
     */
    void validate(Class<?> catalog, ValidationContext context);

}