package com.jeanbarcellos.core.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;

/**
 * Resolver responsável por erros de recursos
 * independentes de framework.
 *
 * @author Jean Barcellos
 */
public class CommonResourceErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        if (isInstanceOf(ex.getClass(), "jakarta.persistence.EntityNotFoundException")) {
            return TechnicalErrorType.RESOURCE_NOT_FOUND;
        }

        if (isInstanceOf(ex.getClass(), "com.fasterxml.jackson.core.JsonParseException")) {
            return TechnicalErrorType.MALFORMED_JSON;
        }

        return null;
    }

}