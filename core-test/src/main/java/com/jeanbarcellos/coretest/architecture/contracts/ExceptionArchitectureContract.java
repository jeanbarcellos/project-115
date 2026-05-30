package com.jeanbarcellos.coretest.architecture.contracts;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;

import org.junit.jupiter.api.Test;

import com.jeanbarcellos.core.exception.ApplicationException;
import com.jeanbarcellos.coretest.support.ArchitecturePackages;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

/**
 * Contrato responsável por proteger
 * a hierarquia de exceções.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public abstract class ExceptionArchitectureContract {

    private final JavaClasses classes = new ClassFileImporter()
            .importPackages(ArchitecturePackages.CORE);

    /**
     * Todas as exceções devem terminar
     * com o sufixo Exception.
     */
    @Test
    void exceptions_shouldHaveExceptionSuffix() {

        classes()
                .that()
                .areAssignableTo(ApplicationException.class)
                .and()
                .doNotHaveSimpleName("ApplicationException")
                .should()
                .haveSimpleNameEndingWith("Exception")
                .check(classes);
    }

    /**
     * Nenhuma exceção pode expor setters.
     */
    @Test
    void exceptions_shouldNotHavePublicSetters() {

        noMethods()
                .that()
                .haveNameMatching("set.*")
                .should()
                .beDeclaredInClassesThat()
                .resideInAPackage(ArchitecturePackages.EXCEPTION)
                .check(classes);
    }
}