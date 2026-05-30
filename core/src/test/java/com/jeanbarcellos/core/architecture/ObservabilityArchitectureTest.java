package com.jeanbarcellos.core.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

/**
 * Regras arquiteturais relacionadas
 * aos componentes de observabilidade.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class ObservabilityArchitectureTest {

    private final JavaClasses classes =
            new ClassFileImporter()
                    .importPackages("com.jeanbarcellos.core");

    /**
     * Garante que ThreadLocal seja utilizado
     * exclusivamente dentro do pacote
     * de observabilidade.
     *
     * @author Jean Barcellos <jeanbarcellos@hotmail.com>
     */
    @Test
    void threadLocal_shouldBeUsedOnlyInsideObservabilityPackage() {

        fields()
                .that()
                .haveRawType(ThreadLocal.class)
                .should()
                .beDeclaredInClassesThat()
                .resideInAPackage("..observability..")
                .check(classes);
    }
}