package com.jeanbarcellos.core.spring.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public class SpringInfrastructureErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        if (ex instanceof org.springframework.beans.factory.BeanCreationException) {
            return TechnicalErrorType.CONFIGURATION_ERROR;
        }

        if (ex instanceof org.springframework.beans.factory.BeanDefinitionStoreException) {
            return TechnicalErrorType.CONFIGURATION_ERROR;
        }

        if (ex instanceof org.springframework.dao.DataAccessResourceFailureException) {
            return TechnicalErrorType.CONNECTION_ERROR;
        }

        if (ex instanceof java.nio.file.AccessDeniedException) {
            return TechnicalErrorType.ACCESS_DENIED;
        }

        if (hasCause(ex, "org.springframework.amqp.AmqpException")) {
            return TechnicalErrorType.MESSAGE_BROKER_ERROR;
        }

        return null;
    }

}
