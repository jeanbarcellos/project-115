package com.jeanbarcellos.core.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;

import org.junit.jupiter.api.Test;

import com.jeanbarcellos.core.exception.ApplicationException;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

/**
 * Regras arquiteturais relacionadas
 * à hierarquia de exceções da plataforma.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class ExceptionArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter()
            .importPackages("com.jeanbarcellos.core");

    /**
     * Garante que todas as exceções possuam
     * o sufixo "Exception".
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
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
     * Garante que exceções não exponham
     * setters públicos.
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
     */
    @Test
    void exceptions_shouldNotHavePublicSetters() {

        noMethods()
                .that()
                .areDeclaredInClassesThat()
                .resideInAPackage("..exception..")
                .should()
                .haveNameMatching("set.*")
                .check(classes);
    }
}