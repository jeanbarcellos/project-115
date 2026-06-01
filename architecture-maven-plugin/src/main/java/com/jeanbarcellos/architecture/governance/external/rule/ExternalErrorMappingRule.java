package com.jeanbarcellos.architecture.governance.external.rule;

import com.jeanbarcellos.architecture.framework.context.ValidationContext;
import com.jeanbarcellos.architecture.framework.report.ValidationCategory;
import com.jeanbarcellos.architecture.framework.rule.ValidationRule;
import com.jeanbarcellos.core.error.ErrorType;
import com.jeanbarcellos.core.error.ExternalErrorType;
import com.jeanbarcellos.core.error.TechnicalErrorType;

/**
 * Garante que qualquer erro externo esteja
 * corretamente mapeado para um erro interno.
 *
 * <p>
 * Esta regra impede que falhas externas sejam
 * propagadas sem tradução para o catálogo oficial
 * de erros da plataforma.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class ExternalErrorMappingRule
        implements ValidationRule<ExternalErrorType> {

    /**
     * Executa a validação.
     *
     * @param target  erro externo validado
     * @param context contexto compartilhado
     */
    @Override
    public void validate(ExternalErrorType target, ValidationContext context) {

        ErrorType errorType = target.getErrorType();

        if (errorType == null) {
            context.getReport().addViolation(
                    ValidationCategory.EXTERNAL_ERROR,
                    "External error without internal mapping: " + target.getCode());
            return;
        }

        if (errorType == TechnicalErrorType.INTERNAL_ERROR) {
            context.getReport().addViolation(
                    ValidationCategory.EXTERNAL_ERROR,
                    "External error cannot map to INTERNAL_ERROR: " + target.getCode());
        }
    }

}