package com.jeanbarcellos.core.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;

/**
 * Resolver responsável por falhas de infraestrutura
 * independentes de framework.
 *
 * @author Jean Barcellos
 */
public class CommonInfrastructureErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

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

        if (ex instanceof java.nio.file.FileSystemException) {
            return TechnicalErrorType.FILE_STORAGE_ERROR;
        }

        if (hasCause(ex, "org.apache.kafka.common.KafkaException")) {
            return TechnicalErrorType.MESSAGE_BROKER_ERROR;
        }

        return null;
    }

}