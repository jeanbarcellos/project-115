package com.jeanbarcellos.core.observability.properties;

import com.jeanbarcellos.core.observability.constants.ObservabilityConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObservabilityProperties {

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    private String correlationHeader = ObservabilityConstants.DEFAULT_CORRELATION_HEADER;

    @Builder.Default
    private boolean responseHeaderEnabled = true;

    @Builder.Default
    private boolean correlationRequired = false;

}