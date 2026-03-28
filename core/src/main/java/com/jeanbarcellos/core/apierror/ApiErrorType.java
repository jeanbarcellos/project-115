package com.jeanbarcellos.core.apierror;

import java.net.URI;

public interface ApiErrorType {

    /**
     * Identidade estável (RFC 7807)
     *
     * @return URI
     */
    URI type();

    /**
     * Resumo humano
     *
     * @return String
     */
    String title(); //

    /**
     * Apenas o número, sem HttpStatus
     *
     * @return int
     */
    int httpStatus();
}