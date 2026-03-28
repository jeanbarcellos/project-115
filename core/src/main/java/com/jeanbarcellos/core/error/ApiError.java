package com.jeanbarcellos.core.error;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private URI type;
    private String title;
    private int status;
    private String detail; // Descrição da ocorrencia
    private URI instance;

    private String correlationId;
    private Instant timestamp;

    /**
     * Extensões permitidas pela RFC 7807
     */
    // @Setter(value = AccessLevel.PRIVATE)
    @JsonAnyGetter
    private Map<String, Object> properties;  // extensão libre (RFC permite)

	// public void setProperty(String name, Object value) {
	// 	this.properties = (this.properties != null ? this.properties : new LinkedHashMap<>());
	// 	this.properties.put(name, value);
	// }

}