package com.jeanbarcellos.core.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;

/**
 * Resolver responsável por integrações externas
 * independentes de framework.
 *
 * @author Jean Barcellos
 */
public class CommonIntegrationErrorResolver
        extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        if (hasCause(ex, "java.net.SocketTimeoutException")
                || hasCause(ex, "java.net.http.HttpTimeoutException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_TIMEOUT;
        }

        if (hasCause(ex, "java.net.ConnectException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_UNAVAILABLE;
        }

        if (hasCause(ex, "java.net.UnknownHostException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_UNAVAILABLE;
        }

        if (hasCause(ex, "javax.net.ssl.SSLException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_UNAVAILABLE;
        }

        return null;
    }

}