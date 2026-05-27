package com.jeanbarcellos.project115.endereco;

import java.util.Map;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.exception.integration.HttpIntegrationException;
import com.jeanbarcellos.core.integration.ExternalErrorType;

public class SerproTeste {

    void test() {
        String url = "http://localhost";

        SerproErrorType externalError = SerproErrorType.NOT_FOUND;
        TechnicalErrorType errorType = SerproErrorMapper.map(externalError);

        Integer status = Integer.valueOf(externalError.getCode());
        String message = "Error calling Serpro API";
        String responseBody = "{}";
        Exception ex = new RuntimeException("error");
        Map<String, Object> metadata = Map.of("endpoint", url);


        throw new HttpIntegrationException(
                "serpro",
                "GET",
                url,
                status,
                message,
                responseBody,
                metadata,
                externalError,
                errorType,
                ex);
    }

}
