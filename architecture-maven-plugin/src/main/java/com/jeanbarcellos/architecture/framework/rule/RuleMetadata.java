package com.jeanbarcellos.architecture.framework.rule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Metadados de uma regra arquitetural.
 *
 * <p>
 * Centraliza informações utilizadas para
 * documentação, rastreabilidade e exibição
 * em relatórios de validação.
 * </p>
 *
 * <p>
 * Os metadados são considerados estáveis e
 * independentes da implementação da regra.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Getter
@Builder
@AllArgsConstructor
public class RuleMetadata {

    /**
     * Código único da regra.
     *
     * <p>
     * Exemplo:
     * </p>
     *
     * <ul>
     *   <li>ERR-001</li>
     *   <li>EXT-002</li>
     *   <li>DDD-001</li>
     * </ul>
     */
    private final String code;

    /**
     * Nome resumido da regra.
     */
    private final String name;

    /**
     * Descrição detalhada da regra.
     */
    private final String description;

    /**
     * Recomendação para correção da violação.
     */
    private final String recommendation;

    public static RuleMetadata of(String code, String name, String description, String recommendation) {
        return RuleMetadata.builder()
                .code(code)
                .name(name)
                .description(description)
                .recommendation(recommendation)
                .build();
    }

}