package com.jeanbarcellos.coretest.architecture.contracts;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

import org.junit.jupiter.api.Test;

import com.jeanbarcellos.coretest.support.ArchitecturePackages;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

/**
 * Contrato relacionado aos componentes
 * de observabilidade.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public abstract class ObservabilityArchitectureContract {

    private final JavaClasses classes = new ClassFileImporter()
            .importPackages(ArchitecturePackages.CORE);

    /**
     * ThreadLocal somente pode existir
     * dentro de observability.
     */
    @Test
    void threadLocal_shouldBeUsedOnlyInsideObservabilityPackage() {

        fields()
                .that()
                .haveRawType(ThreadLocal.class)
                .should()
                .beDeclaredInClassesThat()
                .resideInAPackage(ArchitecturePackages.OBSERVABILITY)
                .check(classes);
    }
}