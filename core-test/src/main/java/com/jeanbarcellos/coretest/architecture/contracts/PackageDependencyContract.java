package com.jeanbarcellos.coretest.architecture.contracts;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

import com.jeanbarcellos.coretest.support.ArchitecturePackages;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

/**
 * Contrato que protege a direção
 * das dependências internas do core.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public abstract class PackageDependencyContract {

    private final JavaClasses classes = new ClassFileImporter()
            .importPackages(ArchitecturePackages.CORE);

    /**
     * error -> exception é proibido.
     */
    @Test
    void errorPackage_shouldNotDependOnExceptionPackage() {

        noClasses()
                .that()
                .resideInAPackage(ArchitecturePackages.ERROR)
                .should()
                .dependOnClassesThat()
                .resideInAPackage(ArchitecturePackages.EXCEPTION)
                .check(classes);
    }

    /**
     * exception -> observability é proibido.
     */
    @Test
    void exceptionPackage_shouldNotDependOnObservabilityPackage() {

        noClasses()
                .that()
                .resideInAPackage(ArchitecturePackages.EXCEPTION)
                .should()
                .dependOnClassesThat()
                .resideInAPackage(ArchitecturePackages.OBSERVABILITY)
                .check(classes);
    }

    /**
     * observability -> exception é proibido.
     */
    @Test
    void observabilityPackage_shouldNotDependOnExceptionPackage() {

        noClasses()
                .that()
                .resideInAPackage(ArchitecturePackages.OBSERVABILITY)
                .should()
                .dependOnClassesThat()
                .resideInAPackage(ArchitecturePackages.EXCEPTION)
                .check(classes);
    }
}