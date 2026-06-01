package com.jeanbarcellos.architecture.framework.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Cache dos resultados de scanner.
 *
 * <p>
 * Evita múltiplas varreduras do classpath
 * para o mesmo contrato durante a execução
 * das validações.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ScanCache {

    /**
     * Cache interno.
     */
    private final Map<Class<?>, Set<Class<?>>> cache = new HashMap<>();

    /**
     * Obtém implementações previamente
     * armazenadas.
     *
     * @param contract contrato pesquisado
     *
     * @return implementações encontradas
     */
    @SuppressWarnings("unchecked")
    public <T> Set<Class<? extends T>> get(
            Class<T> contract) {

        return (Set<Class<? extends T>>) (Set<?>) cache.get(contract);
    }

    /**
     * Verifica se o contrato já está
     * armazenado em cache.
     *
     * @param contract contrato pesquisado
     *
     * @return true quando encontrado
     */
    public boolean contains(
            Class<?> contract) {

        return cache.containsKey(contract);
    }

    /**
     * Armazena implementações.
     *
     * @param contract        contrato pesquisado
     * @param implementations implementações
     */
    @SuppressWarnings("unchecked")
    public <T> void put(
            Class<T> contract,
            Set<Class<? extends T>> implementations) {

        cache.put(
                contract,
                (Set<Class<?>>) (Set<?>) implementations);
    }

}