package com.jeanbarcellos.architecture.framework.validator;

import java.util.List;
import java.util.Set;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.rule.CatalogRule;
import com.jeanbarcellos.architecture.framework.rule.ItemRule;
import com.jeanbarcellos.architecture.framework.scanner.ClassScanner;

/**
 * Implementação base para validação de catálogos.
 *
 * <p>
 * Aplica o padrão Template Method para
 * centralizar o fluxo de validação.
 * </p>
 *
 * <ol>
 * <li>Localiza implementações do contrato;</li>
 * <li>Executa regras do catálogo;</li>
 * <li>Itera sobre os itens do catálogo;</li>
 * <li>Executa regras dos itens.</li>
 * </ol>
 *
 * @param <T> tipo dos itens do catálogo
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public abstract class AbstractCatalogValidator<T> implements ValidatorModule {

    /**
     * Retorna o contrato utilizado pelo scanner.
     *
     * @return contrato pesquisado
     */
    protected abstract Class<T> getContract();

    /**
     * Converte o item encontrado
     * para o tipo esperado.
     *
     * @param constant item do enum
     *
     * @return item convertido
     */
    protected abstract T cast(Object constant);

    /**
     * Regras executadas sobre o catálogo.
     *
     * @return regras de catálogo
     */
    protected abstract List<CatalogRule> getCatalogRules();

    /**
     * Regras executadas sobre cada item.
     *
     * @return regras de item
     */
    protected abstract List<ItemRule<T>> getItemRules();

    /**
     * Executa as validações.
     *
     * @param context contexto compartilhado
     */
    @Override
    public final void validate(ValidationContext context) {

        Set<Class<? extends T>> catalogs;

        if (context.getScanCache().contains(this.getContract())) {
            catalogs = context.getScanCache().get(this.getContract());
        } else {
            catalogs = ClassScanner.findImplementations(this.getContract(), context.getClassLoader());
            context.getScanCache().put(this.getContract(), catalogs);
        }
    }

}