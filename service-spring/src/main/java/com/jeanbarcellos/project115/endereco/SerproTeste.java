package com.jeanbarcellos.project115.endereco;

import java.net.URI;
import java.util.Map;

import com.jeanbarcellos.core.error.ExternalErrorType;
import com.jeanbarcellos.core.exception.integration.HttpIntegrationException;

public class SerproTeste {

    void test() {
        String service = "rest-serpro";
        String method = "GET";
        URI uri = URI.create("https://serpro.gov.br/api/address");
        Integer status = 504;
        Object responsePayload = null;
        String message = "Timeout calling Serpro API";
        Map<String, Object> metadata = Map.of(
                "timeout", "5000ms",
                "traceId", "TRACE-TIMEOUT-001");
        ExternalErrorType externalError = null;
        Throwable cause = new RuntimeException("SocketTimeoutException");

        throw new HttpIntegrationException(
                service,
                method,
                uri,
                status,
                responsePayload,
                message,
                metadata,
                externalError,
                cause);
    }

}
