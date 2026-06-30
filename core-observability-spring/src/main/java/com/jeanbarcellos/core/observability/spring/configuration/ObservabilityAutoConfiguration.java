package com.jeanbarcellos.core.observability.spring.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.jeanbarcellos.core.observability.context.ObservabilityContextProvider;
import com.jeanbarcellos.core.observability.generator.CorrelationIdGenerator;
import com.jeanbarcellos.core.observability.properties.ObservabilityProperties;
import com.jeanbarcellos.core.observability.spring.context.DefaultObservabilityContextManager;
import com.jeanbarcellos.core.observability.spring.context.ObservabilityContextManager;
import com.jeanbarcellos.core.observability.spring.context.ObservabilityContextResolver;
import com.jeanbarcellos.core.observability.spring.filter.CorrelationIdFilter;
import com.jeanbarcellos.core.observability.spring.generator.UuidCorrelationIdGenerator;
import com.jeanbarcellos.core.observability.spring.logging.DefaultMdcContextManager;
import com.jeanbarcellos.core.observability.spring.logging.MdcContextManager;
import com.jeanbarcellos.core.observability.spring.properties.SpringObservabilityProperties;
import com.jeanbarcellos.core.observability.spring.provider.DefaultObservabilityContextProvider;

/**
 * Auto Configuration responsável por registrar toda a infraestrutura de
 * observabilidade para aplicações Spring.
 *
 * <p>
 * Todos os componentes registrados são substituíveis através da declaração
 * de beans do mesmo tipo pelo microsserviço consumidor.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@AutoConfiguration
@EnableConfigurationProperties(SpringObservabilityProperties.class)
@ConditionalOnProperty(prefix = "core.observability", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ObservabilityAutoConfiguration {

    /**
     * Constrói a configuração de observabilidade utilizada pela biblioteca a
     * partir das propriedades carregadas pelo Spring Boot.
     *
     * @param properties propriedades carregadas pelo Spring Boot
     *
     * @return configuração de observabilidade
     */
    @Bean
    @ConditionalOnMissingBean
    ObservabilityProperties observabilityProperties(final SpringObservabilityProperties properties) {
        return properties.toCoreProperties();
    }

    /**
     * Registra o gerador padrão de Correlation ID.
     *
     * @return gerador de Correlation ID
     */
    @Bean
    @ConditionalOnMissingBean
    CorrelationIdGenerator correlationIdGenerator() {

        return new UuidCorrelationIdGenerator();

    }

    /**
     * Registra o resolvedor responsável por criar o contexto inicial de
     * observabilidade.
     *
     * @return resolvedor de contexto
     */
    @Bean
    @ConditionalOnMissingBean
    ObservabilityContextResolver observabilityContextResolver() {

        return new ObservabilityContextResolver();

    }

    /**
     * Registra o componente responsável por sincronizar o contexto de
     * observabilidade com o MDC.
     *
     * @param contextProvider provider do contexto de observabilidade
     *
     * @return gerenciador do MDC
     */
    @Bean
    @ConditionalOnMissingBean
    MdcContextManager mdcContextManager(
            final ObservabilityContextProvider contextProvider) {

        return new DefaultMdcContextManager(contextProvider);

    }

    /**
     * Registra o gerenciador do ciclo de vida do contexto de observabilidade.
     *
     * @param mdcContextManager gerenciador do MDC
     *
     * @return gerenciador do contexto
     */
    @Bean
    @ConditionalOnMissingBean
    ObservabilityContextManager observabilityContextManager(
            final MdcContextManager mdcContextManager) {

        return new DefaultObservabilityContextManager(mdcContextManager);

    }

    /**
     * Registra o provider padrão de contexto de observabilidade.
     *
     * @return provider de contexto
     */
    @Bean
    @ConditionalOnMissingBean
    ObservabilityContextProvider observabilityContextProvider() {

        return new DefaultObservabilityContextProvider();

    }

    /**
     * Registra o filtro responsável por inicializar o contexto de
     * observabilidade de cada requisição HTTP.
     *
     * @param configuration          configuração de observabilidade
     * @param correlationIdGenerator gerador de Correlation ID
     * @param contextResolver        resolvedor do contexto
     * @param contextManager         gerenciador do contexto
     *
     * @return filtro de Correlation ID
     */
    @Bean
    @ConditionalOnMissingBean
    CorrelationIdFilter correlationIdFilter(
            final ObservabilityProperties properties,
            final CorrelationIdGenerator correlationIdGenerator,
            final ObservabilityContextResolver contextResolver,
            final ObservabilityContextManager contextManager) {

        return new CorrelationIdFilter(
                properties,
                correlationIdGenerator,
                contextResolver,
                contextManager);

    }

}