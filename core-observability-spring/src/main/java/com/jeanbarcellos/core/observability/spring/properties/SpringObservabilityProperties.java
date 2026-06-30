package com.jeanbarcellos.core.observability.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.jeanbarcellos.core.observability.constants.ObservabilityConstants;
import com.jeanbarcellos.core.observability.properties.ObservabilityProperties;

import lombok.Data;

/**
 * Spring Boot binding para as propriedades de observabilidade.
 *
 * <p>
 * Esta classe é responsável apenas por realizar o binding das propriedades
 * definidas em {@code application.yml}. O restante da biblioteca utiliza
 * {@link ObservabilityProperties}, mantendo o módulo {@code core}
 * completamente desacoplado do Spring.
 * </p>
 *
 * <pre>
 * core:
 *   observability:
 *     enabled: true
 *     correlation:
 *       header: correlation-id
 *       required: false
 *       response-header-enabled: true
 * </pre>
 *
 * @author Jean Barcellos
 */
@Data
@ConfigurationProperties(prefix = "core.observability")
public class SpringObservabilityProperties {

    /**
     * Habilita ou desabilita a biblioteca de observabilidade.
     */
    private boolean enabled = true;

    /**
     * Configurações relacionadas ao Correlation ID.
     */
    private final Correlation correlation = new Correlation();

    /**
     * Converte as propriedades do Spring para o modelo utilizado pelo módulo core.
     *
     * @return propriedades de observabilidade
     */
    public ObservabilityProperties toCoreProperties() {

        final ObservabilityProperties properties = ObservabilityProperties.builder()
                .enabled(this.enabled)
                .build();

        properties.getCorrelation().setHeader(this.correlation.getHeader());
        properties.getCorrelation().setRequired(this.correlation.isRequired());
        properties.getCorrelation().setResponseHeaderEnabled(
                this.correlation.isResponseHeaderEnabled());

        return properties;
    }

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