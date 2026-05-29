package com.jeanbarcellos.core.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
class ErrorCategoryTest {

    @Test
    void getCode_whenCategoryIsTechnical_shouldReturnTechnicalCode() {

        // Arrange
        ErrorCategory category = ErrorCategory.TECHNICAL;

        // Act
        String result = category.getCode();

        // Assert
        assertEquals("technical", result);
    }

    @Test
    void values_whenAllCategoriesAreLoaded_shouldHaveUniqueCodes() {

        // Arrange
        Set<String> codes = new HashSet<>();

        // Act / Assert
        for (ErrorCategory category : ErrorCategory.values()) {
            assertTrue(codes.add(category.getCode()));
        }
    }
}