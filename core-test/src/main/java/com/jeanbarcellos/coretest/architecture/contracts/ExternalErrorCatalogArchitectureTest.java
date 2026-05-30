package com.jeanbarcellos.coretest.architecture.contracts;

import org.junit.jupiter.api.Test;

import com.jeanbarcellos.coretest.architecture.assertions.ErrorCatalogAssertions;
import com.jeanbarcellos.coretest.architecture.scanner.ExternalErrorCatalogScanner;

/**
 * Contrato arquitetural para catálogos de erros
 * de integrações externas.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public abstract class ExternalErrorCatalogArchitectureTest {

    @Test
    void externalErrorTypes_shouldBeImplementedOnlyByEnums() {

        ExternalErrorCatalogScanner.scan()
                .forEach(ErrorCatalogAssertions::assertEnum);
    }

}