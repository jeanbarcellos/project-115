package com.jeanbarcellos.architecture.framework.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;

/**
 * Regra aplicada aos itens
 * de um catálogo.
 *
 * @param <T> tipo validado
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public interface ItemRule<T> extends ValidationRule {

    /**
     * Executa a validação.
     *
     * @param item    item validado
     * @param context contexto compartilhado
     */
    void validate(T item, ValidationContext context);

}