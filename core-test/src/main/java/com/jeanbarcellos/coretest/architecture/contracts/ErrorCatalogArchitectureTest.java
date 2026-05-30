package com.jeanbarcellos.coretest.architecture.contracts;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.jeanbarcellos.core.error.ErrorType;
import com.jeanbarcellos.coretest.architecture.assertions.ErrorCatalogAssertions;
import com.jeanbarcellos.coretest.architecture.scanner.ErrorCatalogScanner;

/**
 * Contrato arquitetural dos catálogos de erro.
 *
 * <p>
 * Todo microsserviço deve herdar esta classe para
 * validar automaticamente seus catálogos de erro.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public abstract class ErrorCatalogArchitectureTest {

    /**
     * Garante que implementações de ErrorType
     * sejam realizadas exclusivamente por enums.
     */
    @Test
    void errorTypes_shouldBeImplementedOnlyByEnums() {

        ErrorCatalogScanner.scan()
                .forEach(ErrorCatalogAssertions::assertEnum);
    }

    /**
     * Garante unicidade global dos códigos.
     */
    @Test
    void errorCodes_shouldBeUnique() {

        Set<String> codes = new HashSet<>();

        for (Class<? extends ErrorType> clazz : ErrorCatalogScanner.scan()) {

            for (Object constant : clazz.getEnumConstants()) {

                ErrorType error = (ErrorType) constant;

                ErrorCatalogAssertions.assertUniqueCode(
                        codes,
                        error.getCode());
            }
        }
    }

    /**
     * Garante utilização de kebab-case.
     */
    @Test
    void errorCodes_shouldUseKebabCase() {

        for (Class<? extends ErrorType> clazz : ErrorCatalogScanner.scan()) {

            for (Object constant : clazz.getEnumConstants()) {

                ErrorType error = (ErrorType) constant;

                ErrorCatalogAssertions.assertKebabCase(
                        error.getCode());
            }
        }
    }

    /**
     * Garante status HTTP válidos.
     */
    @Test
    void errorTypes_shouldHaveValidHttpStatus() {

        for (Class<? extends ErrorType> clazz : ErrorCatalogScanner.scan()) {

            for (Object constant : clazz.getEnumConstants()) {

                ErrorType error = (ErrorType) constant;

                ErrorCatalogAssertions.assertHttpStatus(
                        error.getHttpStatus());
            }
        }
    }

    /**
     * Garante preenchimento dos atributos obrigatórios.
     */
    @Test
    void errorTypes_shouldHaveMandatoryFieldsFilled() {

        for (Class<? extends ErrorType> clazz : ErrorCatalogScanner.scan()) {

            for (Object constant : clazz.getEnumConstants()) {

                ErrorType error = (ErrorType) constant;

                ErrorCatalogAssertions.assertNotBlank(
                        error.getCode(),
                        "code");

                ErrorCatalogAssertions.assertNotBlank(
                        error.getTitle(),
                        "title");
            }
        }
    }
}