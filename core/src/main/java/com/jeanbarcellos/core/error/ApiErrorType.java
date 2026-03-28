package com.jeanbarcellos.core.error;

import java.net.URI;

public interface ApiErrorType {

    /**
     * Código curto usado em /problems/{cod}
     * Ex: user-note-found
     *
     * @return
     */
    String code();

    /**
     * URI RFC 7807 que identifica o tipo do problema
     *
     * @return URI
     */
    URI type();

    /**
     * Resumo humano e estável
     *
     * @return String
     */
    String title(); //

    /**
     * Status HTTP numérico (core não depende de HttpStatus)
     *
     * @return int
     */
    int httpStatus();
}