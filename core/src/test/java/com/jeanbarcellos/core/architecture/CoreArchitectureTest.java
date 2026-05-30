package com.jeanbarcellos.core.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

/**
 * Regras arquiteturais globais da biblioteca core.
 *
 * <p>
 * O objetivo destes testes é garantir que a biblioteca permaneça
 * completamente independente de frameworks, aplicações consumidoras
 * e tecnologias específicas de execução.
 * </p>
 *
 * <p>
 * Como a biblioteca core é compartilhada entre múltiplos serviços,
 * qualquer acoplamento indevido pode comprometer sua reutilização
 * e evolução.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class CoreArchitectureTest {

    private static final String BASE_PACKAGE = "com.jeanbarcellos.core";

    private final JavaClasses classes = new ClassFileImporter()
            .importPackages(BASE_PACKAGE);

    /**
     * Garante que a biblioteca core permaneça
     * independente do Spring Framework.
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
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
     * Garante que a biblioteca core permaneça
     * independente do Quarkus.
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
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
     * Garante que a biblioteca core permaneça
     * independente de JAX-RS.
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
     */
    @Test
    void core_shouldNotDependOnJaxRs() {

        noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("jakarta.ws.rs..")
                .check(classes);
    }

    /**
     * Garante que a biblioteca core permaneça
     * independente da API Servlet.
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
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
     * Garante que a biblioteca core não dependa
     * de módulos consumidores da plataforma.
     *
     * <p>
     * Esta regra evita dependências cíclicas e
     * preserva o isolamento da biblioteca.
     * </p>
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
     */
    @Test
    void core_shouldNotDependOnProjectModules() {

        noClasses()
                .that()
                .resideInAPackage("..core..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "..project115..",
                        "..service.spring..",
                        "..service.quarkus..")
                .check(classes);
    }
}