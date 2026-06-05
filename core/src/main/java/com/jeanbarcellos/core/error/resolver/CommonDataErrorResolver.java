package com.jeanbarcellos.core.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;

/**
 * Resolver responsável por erros de persistência
 * independentes de framework.
 *
 * @author Jean Barcellos
 */
public class CommonDataErrorResolver
        extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        if (ex instanceof java.sql.SQLTimeoutException) {
            return TechnicalErrorType.TIMEOUT;
        }

        if (ex instanceof java.sql.SQLException) {
            return TechnicalErrorType.DATABASE_ERROR;
        }

        if (ex instanceof com.fasterxml.jackson.databind.exc.MismatchedInputException) {
            return TechnicalErrorType.DESERIALIZATION_ERROR;
        }

        return null;
    }

}