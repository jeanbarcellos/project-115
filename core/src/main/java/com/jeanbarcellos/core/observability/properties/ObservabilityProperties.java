package com.jeanbarcellos.core.observability.properties;

import com.jeanbarcellos.core.observability.constants.ObservabilityConstants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObservabilityProperties {

    /**
     * Habilita ou desabilita a biblioteca de observabilidade.
     */
    @Builder.Default
    private boolean enabled = true;

    /**
     * Configurações relacionadas ao Correlation ID.
     */
    @Builder.Default
    private final Correlation correlation = new Correlation();

    /**
     * Configurações relacionadas ao Correlation ID.
     */
    @Data
    public static class Correlation {

        /**
         * Nome do header HTTP utilizado para transportar o Correlation ID.
         */
        private String header = ObservabilityConstants.DEFAULT_CORRELATION_HEADER;

        /**
         * Indica se o header é obrigatório.
         *
         * Caso falso, será gerado automaticamente quando ausente.
         */
        private boolean required = false;

        /**
         * Indica se o Correlation ID deve ser devolvido na resposta HTTP.
         */
        private boolean responseHeaderEnabled = true;
    }

}