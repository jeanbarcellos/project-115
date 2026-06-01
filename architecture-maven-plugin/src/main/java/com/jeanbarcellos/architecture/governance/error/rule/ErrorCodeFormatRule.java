package com.jeanbarcellos.architecture.governance.error.rule;

import java.util.regex.Pattern;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.report.ValidationModule;
import com.jeanbarcellos.architecture.framework.rule.ItemRule;
import com.jeanbarcellos.architecture.framework.rule.RuleMetadata;
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
     * {@inheritDoc}
     */
    @Override
    public RuleMetadata metadata() {

        return RuleMetadata.builder()
                .code("ERR-004")
                .name("Formato do código inválido")
                .description("Os códigos dos erros devem seguir o padrão definido pela arquitetura.")
                .recommendation("Utilize letras minúsculas e hífens  conforme o padrão adotado.")
                .build();
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