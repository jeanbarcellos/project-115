package com.jeanbarcellos.architecture.framework.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Categorias de validações arquiteturais.
 *
 * <p>
 * Utilizadas para classificação e agrupamento
 * das violações encontradas durante o processo
 * de validação.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@RequiredArgsConstructor
public enum ValidationCategory {

    /**
     * Regras relacionadas ao catálogo ErrorType.
     */
    ERROR_TYPE("ERROR-TYPE"),

    /**
     * Regras relacionadas ao catálogo ExternalErrorType.
     */
    EXTERNAL_ERROR("EXTERNAL-ERROR"),

    /**
     * Regras relacionadas às exceções.
     */
    EXCEPTION("EXCEPTION"),

    /**
     * Regras relacionadas aos pacotes.
     */
    PACKAGE("PACKAGE"),

    /**
     * Regras relacionadas a DDD.
     */
    DDD("DDD"),

    /**
     * Regras relacionadas à arquitetura hexagonal.
     */
    HEXAGONAL("HEXAGONAL"),

    /**
     * Regras relacionadas à observabilidade.
     */
    OBSERVABILITY("OBSERVABILITY");

    /**
     * Código da categoria.
     */
    private final String code;

}