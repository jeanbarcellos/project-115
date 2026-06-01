package com.jeanbarcellos.architecture.governance.error.rule;

import java.util.regex.Pattern;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.report.ValidationModule;
import com.jeanbarcellos.architecture.framework.rule.ItemRule;
import com.jeanbarcellos.architecture.framework.rule.RuleMetadata;
import com.jeanbarcellos.architecture.framework.rule.ValidationSeverity;
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
public class ErrorCodeFormatRule implements ItemRule<ErrorType> {

    /**
     * Expressão regular utilizada para validação.
     */
    @SuppressWarnings("java:S5998")
    private static final Pattern KEBAB_CASE_PATTERN = Pattern.compile("[a-z0-9]+(-[a-z0-9]+)*");

    /**
     * Metatados da Regra
     */
    private static final RuleMetadata METADATA = RuleMetadata.of(
            "ERR-001",
            "ErrorType deve ser enum",
            "Toda implementação ...",
            "Substitua implementações ...",
            ValidationSeverity.ERROR);

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleMetadata metadata() {
        return METADATA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ErrorType target, ValidationContext context) {

        String code = target.getCode();

        if (code == null || !KEBAB_CASE_PATTERN.matcher(code).matches()) {

            context.addViolation(
                    ValidationModule.ERROR,
                    ValidationCategory.CONTRACT,
                    this,
                    target.getClass(),
                    ((Enum<?>) target).name(),
                    "Invalid error code format: " + code);
        }
    }

}