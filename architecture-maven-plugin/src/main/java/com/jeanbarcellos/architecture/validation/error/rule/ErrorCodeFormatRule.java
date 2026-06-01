package com.jeanbarcellos.architecture.validation.error.rule;

import java.util.regex.Pattern;

import com.jeanbarcellos.architecture.validation.context.ValidationContext;
import com.jeanbarcellos.architecture.validation.report.ValidationCategory;
import com.jeanbarcellos.architecture.validation.rule.ValidationRule;
import com.jeanbarcellos.core.error.ErrorType;

/**
 * Garante que os códigos de erro utilizem
 * o padrão kebab-case.
 *
 * <p>
 * Exemplos válidos:
 * </p>
 *
 * <ul>
 * <li>user-not-found</li>
 * <li>wallet-not-found</li>
 * </ul>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ErrorCodeFormatRule
        implements ValidationRule<ErrorType> {

    /**
     * Expressão regular utilizada para validação.
     */
    private static final Pattern KEBAB_CASE_PATTERN = Pattern.compile("[a-z0-9]+(-[a-z0-9]+)*");

    /**
     * Executa a validação.
     *
     * @param target  erro validado
     * @param context contexto compartilhado
     */
    @Override
    public void validate(ErrorType target, ValidationContext context) {

        String code = target.getCode();

        if (code == null || !KEBAB_CASE_PATTERN.matcher(code).matches()) {
            context.getReport().addViolation(
                    ValidationCategory.ERROR_TYPE,
                    "Invalid error code format: " + code);
        }
    }

}