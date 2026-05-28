package com.jeanbarcellos.core.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Representa o nível de log
 * utilizado na observabilidade.
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@RequiredArgsConstructor
public enum ErrorLogLevel {

    TRACE("trace"),

    DEBUG("debug"),

    INFO("info"),

    WARN("warn"),

    ERROR("error");

    /**
     * Código textual do nível.
     */
    private final String code;
}