package com.jeanbarcellos.core.spring.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public class SpringConflictErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        if (ex instanceof org.springframework.dao.OptimisticLockingFailureException) {
            return TechnicalErrorType.OPTIMISTIC_LOCK_ERROR;
        }

        if (ex instanceof org.springframework.dao.PessimisticLockingFailureException) {
            return TechnicalErrorType.PESSIMISTIC_LOCK_ERROR;
        }

        if (ex instanceof org.springframework.dao.CannotAcquireLockException) {
            return TechnicalErrorType.PESSIMISTIC_LOCK_ERROR;
        }

        if (ex instanceof org.springframework.dao.DeadlockLoserDataAccessException) {
            return TechnicalErrorType.PESSIMISTIC_LOCK_ERROR;
        }

        return null;
    }

}
