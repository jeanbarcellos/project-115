package com.jeanbarcellos.architecture.validation.validator;

import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;

import com.jeanbarcellos.architecture.scanner.ClassScanner;
import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;

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
public abstract class AbstractCatalogValidator<T> {

    /**
     * ClassLoader do projeto validado.
     */
    private final ClassLoader classLoader;

    /**
     * Cria uma nova instância.
     *
     * @param classLoader class loader do projeto
     */
    protected AbstractCatalogValidator(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Executa a validação completa.
     *
     * @param context contexto compartilhado
     *
     * @throws MojoExecutionException quando
     *                                alguma regra falha
     */
    public final void validate(
            ValidationContext context)
            throws MojoExecutionException {

        Set<Class<? extends T>> catalogs = ClassScanner.findImplementations(
                this.getContract(),
                classLoader);

        for (Class<? extends T> catalog : catalogs) {

            this.validateCatalog(catalog, context);

            for (Object constant : catalog.getEnumConstants()) {

                T item = this.cast(constant);

                this.validateItem(item, context);
            }
        }
    }

    /**
     * Executa regras do catálogo.
     *
     * @param catalog catálogo encontrado
     * @param context contexto compartilhado
     *
     * @throws MojoExecutionException quando alguma regra falha
     */
    private void validateCatalog(
            Class<?> catalog,
            ValidationContext context)
            throws MojoExecutionException {

        for (ValidationRule<Class<?>> rule : this.getCatalogRules()) {
            rule.validate(catalog, context);
        }
    }

    /**
     * Executa regras dos itens.
     *
     * @param item    item do catálogo
     * @param context contexto compartilhado
     *
     * @throws MojoExecutionException quando alguma regra falha
     */
    private void validateItem(
            T item,
            ValidationContext context)
            throws MojoExecutionException {

        for (ValidationRule<T> rule : this.getItemRules()) {
            rule.validate(item, context);
        }
    }

    /**
     * Retorna o contrato utilizado pelo scanner.
     *
     * @return contrato pesquisado
     */
    protected abstract Class<T> getContract();

    /**
     * Converte a constante localizada.
     *
     * @param constant constante encontrada
     * @return item convertido
     */
    protected abstract T cast(Object constant);

    /**
     * Retorna as regras aplicadas ao catálogo.
     *
     * @return lista de regras
     */
    protected abstract List<ValidationRule<Class<?>>> getCatalogRules();

    /**
     * Retorna as regras aplicadas aos itens.
     *
     * @return lista de regras
     */
    protected abstract List<ValidationRule<T>> getItemRules();

}