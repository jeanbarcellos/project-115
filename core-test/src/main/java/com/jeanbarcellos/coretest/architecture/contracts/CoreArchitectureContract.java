package com.jeanbarcellos.coretest.architecture.contracts;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

import com.jeanbarcellos.coretest.support.ArchitecturePackages;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

/**
 * Contrato responsável por garantir
 * que a biblioteca core permaneça
 * agnóstica a frameworks.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public abstract class CoreArchitectureContract {

    private final JavaClasses classes = new ClassFileImporter()
            .importPackages(ArchitecturePackages.CORE);

    /**
     * Garante independência do Spring.
     */
    @Test
    void core_shouldNotDependOnSpring() {

        noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("org.springframework..")
                .check(classes);
    }

    /**
     * Garante independência do Quarkus.
     */
    @Test
    void core_shouldNotDependOnQuarkus() {

        noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("io.quarkus..")
                .check(classes);
    }

    /**
     * Garante independência do Servlet.
     */
    @Test
    void core_shouldNotDependOnServletApi() {

        noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "jakarta.servlet..",
                        "javax.servlet..")
                .check(classes);
    }

    /**
     * Garante independência do JAX-RS.
     */
    @Test
    void core_shouldNotDependOnJaxRs() {

        noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("jakarta.ws.rs..")
                .check(classes);
    }
}