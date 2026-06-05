package com.jeanbarcellos.core.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;

/**
 * Resolver responsável por erros de concorrência
 * independentes de framework.
 *
 * @author Jean Barcellos
 */
public class CommonConflictErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        if (hasCause(ex, "jakarta.persistence.OptimisticLockException")) {
            return TechnicalErrorType.OPTIMISTIC_LOCK_ERROR;
        }

        if (hasCause(ex, "jakarta.persistence.PessimisticLockException")) {
            return TechnicalErrorType.PESSIMISTIC_LOCK_ERROR;
        }

        return null;
    }

}