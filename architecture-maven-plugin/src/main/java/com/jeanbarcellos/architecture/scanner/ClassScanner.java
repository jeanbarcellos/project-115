package com.jeanbarcellos.architecture.scanner;

import java.util.Set;
import java.util.stream.Collectors;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

/**
 * Responsável por localizar implementações
 * de contratos no classpath do projeto.
 *
 * <p>
 * Utiliza ClassGraph para realizar
 * o escaneamento das classes compiladas.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public final class ClassScanner {

    private ClassScanner() {
    }

    /**
     * Localiza implementações de um contrato.
     *
     * @param contract    contrato pesquisado
     * @param classLoader class loader do projeto
     *
     * @param <T>         tipo do contrato
     *
     * @return implementações encontradas
     */
    public static <T> Set<Class<? extends T>> findImplementations(
            Class<T> contract,
            ClassLoader classLoader) {

        try (ScanResult scanResult = new ClassGraph()
                .overrideClassLoaders(classLoader)
                .enableClassInfo()
                .scan()) {

            return scanResult
                    .getClassesImplementing(contract.getName())
                    .loadClasses(contract)
                    .stream()
                    .map(clazz -> (Class<? extends T>) clazz)
                    .collect(Collectors.toSet());
        }
    }

}