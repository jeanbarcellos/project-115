package com.jeanbarcellos.core.observability.spring.configuration;

@AutoConfiguration
@EnableConfigurationProperties(SpringObservabilityProperties.class)
@ConditionalOnProperty(prefix = "core.observability", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ObservabilityAutoConfiguration {

    @Bean
    public ObservabilityProperties observabilityProperties(
            SpringObservabilityProperties springProperties) {

        return springProperties.toProperties();
    }

    @Bean
    public CorrelationIdFilter correlationIdFilter(
            ObservabilityProperties properties) {

        return new CorrelationIdFilter(properties);
    }

    @Bean
    public ObservabilityContextHolder observabilityContextHolder() {

        return new DefaultObservabilityContextHolder();
    }

}