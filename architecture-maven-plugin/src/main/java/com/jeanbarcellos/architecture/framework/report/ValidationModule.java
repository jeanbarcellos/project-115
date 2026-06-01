package com.jeanbarcellos.architecture.framework.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Representa os módulos de governança
 * arquitetural suportados pelo framework.
 *
 * <p>
 * Cada módulo agrupa regras relacionadas
 * a uma determinada área da arquitetura.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@RequiredArgsConstructor
public enum ValidationModule {

    /**
     * Governança dos catálogos ErrorType.
     */
    ERROR("ERROR"),

    /**
     * Governança dos catálogos ExternalErrorType.
     */
    EXTERNAL("EXTERNAL"),

    /**
     * Governança das exceções.
     */
    EXCEPTION("EXCEPTION"),

    /**
     * Governança de DDD.
     */
    DDD("DDD"),

    /**
     * Governança de dependências entre pacotes.
     */
    PACKAGE("PACKAGE"),

    /**
     * Governança da arquitetura hexagonal.
     */
    HEXAGONAL("HEXAGONAL"),

    /**
     * Governança de observabilidade.
     */
    OBSERVABILITY("OBSERVABILITY");

    /**
     * Código textual do módulo.
     */
    private final String code;

}