package com.jeanbarcellos.architecture.scanner;

import java.util.Set;
import java.util.stream.Collectors;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

/**
 * Scanner genérico de classes.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public final class ClassScanner {

    private ClassScanner() {
    }

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