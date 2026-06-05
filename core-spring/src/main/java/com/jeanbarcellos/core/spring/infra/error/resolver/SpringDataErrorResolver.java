package com.jeanbarcellos.core.spring.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public class SpringDataErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        if (ex instanceof jakarta.persistence.QueryTimeoutException) {
            return TechnicalErrorType.TIMEOUT;
        }

        if (ex instanceof org.springframework.web.context.request.async.AsyncRequestTimeoutException) {
            return TechnicalErrorType.TIMEOUT;
        }

        if (ex instanceof org.springframework.transaction.TransactionException) {
            return TechnicalErrorType.DATABASE_ERROR;
        }

        if (ex instanceof org.springframework.dao.DataIntegrityViolationException) {
            return TechnicalErrorType.DATA_INTEGRITY_VIOLATION;
        }

        if (ex instanceof org.springframework.http.converter.HttpMessageNotWritableException) {
            return TechnicalErrorType.SERIALIZATION_ERROR;
        }

        return null;
    }

}
