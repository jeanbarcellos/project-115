package com.jeanbarcellos.core.spring.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public class SpringInfraestructureErrorResolver extends AbstractErrorResolver {

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

        if (ex instanceof java.net.ConnectException) {
            return TechnicalErrorType.CONNECTION_ERROR;
        }

        if (ex instanceof java.net.UnknownHostException) {
            return TechnicalErrorType.CONNECTION_ERROR;
        }

        if (ex instanceof java.net.SocketException) {
            return TechnicalErrorType.CONNECTION_ERROR;
        }

        if (ex instanceof java.io.FileNotFoundException) {
            return TechnicalErrorType.FILE_STORAGE_ERROR;
        }

        if (ex instanceof java.nio.file.AccessDeniedException) {
            return TechnicalErrorType.ACCESS_DENIED;
        }

        if (ex instanceof java.nio.file.FileSystemException) {
            return TechnicalErrorType.FILE_STORAGE_ERROR;
        }

        if (hasCause(ex, "org.apache.kafka.common.KafkaException")) {
            return TechnicalErrorType.MESSAGE_BROKER_ERROR;
        }

        if (hasCause(ex, "org.springframework.amqp.AmqpException")) {
            return TechnicalErrorType.MESSAGE_BROKER_ERROR;
        }

        return null;
    }

}
