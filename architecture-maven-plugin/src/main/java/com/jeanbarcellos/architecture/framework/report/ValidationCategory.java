package com.jeanbarcellos.architecture.framework.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Categorias de validações arquiteturais.
 *
 * <p>
 * Permitem classificar o tipo de regra
 * executada dentro de cada módulo
 * de governança.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@RequiredArgsConstructor
public enum ValidationCategory {

    /**
     * Regras relacionadas à definição
     * estrutural do catálogo.
     */
    CATALOG("CATALOG"),

    /**
     * Regras relacionadas ao contrato
     * dos elementos.
     */
    CONTRACT("CONTRACT"),

    /**
     * Regras relacionadas a mapeamentos.
     */
    MAPPING("MAPPING"),

    /**
     * Regras relacionadas a dependências.
     */
    DEPENDENCY("DEPENDENCY"),

    /**
     * Regras relacionadas a organização
     * em camadas.
     */
    LAYERING("LAYERING"),

    /**
     * Regras relacionadas à nomenclatura.
     */
    NAMING("NAMING"),

    /**
     * Regras relacionadas à estrutura
     * arquitetural.
     */
    STRUCTURE("STRUCTURE"),

    /**
     * Regras relacionadas a anotações.
     */
    ANNOTATION("ANNOTATION"),

    /**
     * Regras relacionadas à observabilidade.
     */
    OBSERVABILITY("OBSERVABILITY");

    /**
     * Código textual da categoria.
     */
    private final String code;

}