package com.jeanbarcellos.core.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

/**
 * Regras arquiteturais relacionadas
 * às dependências entre pacotes do core.
 *
 * <p>
 * O objetivo é preservar a direção correta
 * das dependências e evitar acoplamentos
 * indevidos entre os módulos internos.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class PackageDependencyArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter()
            .importPackages("com.jeanbarcellos.core");

    /**
     * Garante que o pacote de erros permaneça
     * independente da hierarquia de exceções.
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
     */
    @Test
    void errorPackage_shouldNotDependOnExceptionPackage() {

        noClasses()
                .that()
                .resideInAPackage("..error..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..exception..")
                .check(classes);
    }

    /**
     * Garante que exceções não dependam
     * de componentes de observabilidade.
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
     */
    @Test
    void exceptionPackage_shouldNotDependOnObservabilityPackage() {

        noClasses()
                .that()
                .resideInAPackage("..exception..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..observability..")
                .check(classes);
    }

    /**
     * Garante que observabilidade não dependa
     * da hierarquia de exceções.
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
     */
    @Test
    void observabilityPackage_shouldNotDependOnExceptionPackage() {

        noClasses()
                .that()
                .resideInAPackage("..observability..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..exception..")
                .check(classes);
    }
}