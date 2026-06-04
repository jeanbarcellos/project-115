package com.jeanbarcellos.core.quarkus.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public class QuarkusConflictErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        if (isInstanceOf(ex.getClass(), "jakarta.persistence.OptimisticLockException")) {
            return TechnicalErrorType.OPTIMISTIC_LOCK_ERROR;
        }
        if (isInstanceOf(ex.getClass(), "jakarta.persistence.PessimisticLockException")) {
            return TechnicalErrorType.PESSIMISTIC_LOCK_ERROR;
        }

        return null;
    }

}
