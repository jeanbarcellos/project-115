package com.jeanbarcellos.core.error;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class TechnicalErrorTypeTest {

    @Test
    void values_whenLoaded_shouldHaveUniqueCodes() {

        // Arrange
        Set<String> codes = new HashSet<>();

        // Act / Assert
        for (TechnicalErrorType error : TechnicalErrorType.values()) {
            assertTrue(codes.add(error.getCode()));
        }
    }

    @Test
    void values_whenLoaded_shouldHaveValidHttpStatus() {

        // Act / Assert
        for (TechnicalErrorType error : TechnicalErrorType.values()) {

            assertTrue(error.getHttpStatus() >= 400);
            assertTrue(error.getHttpStatus() <= 599);
        }
    }

    @Test
    void values_whenLoaded_shouldHaveNonBlankCode() {

        // Act / Assert
        for (TechnicalErrorType error : TechnicalErrorType.values()) {

            assertNotNull(error.getCode());
            assertFalse(error.getCode().isBlank());
        }
    }

    @Test
    void values_whenLoaded_shouldHaveNonBlankTitle() {

        // Act / Assert
        for (TechnicalErrorType error : TechnicalErrorType.values()) {

            assertNotNull(error.getTitle());
            assertFalse(error.getTitle().isBlank());
        }
    }
}