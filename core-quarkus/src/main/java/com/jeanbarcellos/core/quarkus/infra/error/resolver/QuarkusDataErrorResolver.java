package com.jeanbarcellos.core.quarkus.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public class QuarkusDataErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        if (isInstanceOf(ex.getClass(), "jakarta.persistence.QueryTimeoutException")) {
            return TechnicalErrorType.TIMEOUT;
        }

        if (hasCause(ex, "org.hibernate.QueryTimeoutException")) {
            return TechnicalErrorType.TIMEOUT;
        }

        // Violação de constraint de banco de dados (Unique Key, Foreign Key) do
        // Hibernate
        if (isInstanceOf(ex.getClass(), "org.hibernate.exception.ConstraintViolationException")) {
            return TechnicalErrorType.DATA_INTEGRITY_VIOLATION;
        }

        if (ex instanceof com.fasterxml.jackson.databind.JsonMappingException) {
            return TechnicalErrorType.DESERIALIZATION_ERROR;
        }

        return null;
    }

}
