package com.jeanbarcellos.coretest.architecture.scanner;

import java.util.Set;
import java.util.stream.Collectors;

import com.jeanbarcellos.core.error.ExternalErrorType;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

/**
 * Scanner responsável por localizar implementações de
 * {@link ExternalErrorType}.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public final class ExternalErrorCatalogScanner {

    private ExternalErrorCatalogScanner() {
    }

    public static Set<Class<? extends ExternalErrorType>> scan() {

        try (ScanResult scan = new ClassGraph()
                .enableClassInfo()
                .scan()) {

            return scan
                    .getClassesImplementing(ExternalErrorType.class.getName())
                    .loadClasses(ExternalErrorType.class)
                    .stream()
                    .map(clazz -> (Class<? extends ExternalErrorType>) clazz)
                    .collect(Collectors.toSet());
        }
    }
    
}