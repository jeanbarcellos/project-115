package com.jeanbarcellos.core.error;

import java.net.URI;

public interface ApiErrorType {

    /**
     * Código do erro
     *
     * @return
     */
    String code();

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