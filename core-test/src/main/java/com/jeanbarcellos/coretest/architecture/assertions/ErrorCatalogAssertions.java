package com.jeanbarcellos.coretest.architecture.assertions;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Assertivas reutilizáveis para validação dos catálogos
 * de erro da plataforma.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public final class ErrorCatalogAssertions {

    private static final Pattern KEBAB_CASE = Pattern.compile("[a-z0-9]+(-[a-z0-9]+)*");

    private ErrorCatalogAssertions() {
    }

    public static void assertEnum(Class<?> clazz) {

        assertTrue(
                clazz.isEnum(),
                () -> clazz.getName() + " must be an enum");
    }

    public static void assertUniqueCode(
            Set<String> codes,
            String code) {

        assertTrue(
                codes.add(code),
                () -> "Duplicated error code: " + code);
    }

    public static void assertKebabCase(String code) {

        assertTrue(
                KEBAB_CASE.matcher(code).matches(),
                () -> "Invalid error code: " + code);
    }

    public static void assertHttpStatus(int status) {

        assertTrue(
                status >= 400 && status <= 599,
                () -> "Invalid HTTP status: " + status);
    }

    public static void assertNotBlank(
            String value,
            String field) {

        assertNotNull(value);

        assertFalse(
                value.isBlank(),
                () -> field + " cannot be blank");
    }
}