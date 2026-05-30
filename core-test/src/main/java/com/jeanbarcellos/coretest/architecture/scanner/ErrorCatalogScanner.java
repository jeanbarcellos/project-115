package com.jeanbarcellos.coretest.architecture.scanner;

import java.util.Set;
import java.util.stream.Collectors;

import com.jeanbarcellos.core.error.ErrorType;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

/**
 * Scanner responsável por localizar implementações de {@link ErrorType}
 * presentes no classpath da aplicação.
 *
 * <p>
 * Utilizado pelos testes arquiteturais para validar todos os catálogos
 * de erro da plataforma sem necessidade de configuração manual.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public final class ErrorCatalogScanner {

    private ErrorCatalogScanner() {
    }

    public static Set<Class<? extends ErrorType>> scan() {

        try (ScanResult scan = new ClassGraph()
                .enableClassInfo()
                .scan()) {

            return scan
                    .getClassesImplementing(ErrorType.class.getName())
                    .loadClasses(ErrorType.class)
                    .stream()
                    .map(clazz -> (Class<? extends ErrorType>) clazz)
                    .collect(Collectors.toSet());
        }
    }
}