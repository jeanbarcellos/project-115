package com.jeanbarcellos.core.observability.spring.properties;

import com.jeanbarcellos.core.observability.constants.ObservabilityConstants;
import com.jeanbarcellos.core.observability.properties.ObservabilityProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "core.observability")
public class SpringObservabilityProperties {

    private boolean enabled = true;

    private String correlationHeader =
            ObservabilityConstants.DEFAULT_CORRELATION_HEADER;

    private boolean responseHeaderEnabled = true;

    private boolean correlationRequired = false;

    public ObservabilityProperties toProperties() {

        return ObservabilityProperties.builder()
                .enabled(enabled)
                .correlationHeader(correlationHeader)
                .responseHeaderEnabled(responseHeaderEnabled)
                .correlationRequired(correlationRequired)
                .build();
    }

}