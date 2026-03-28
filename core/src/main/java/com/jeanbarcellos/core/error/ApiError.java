package com.jeanbarcellos.core.error;

import java.net.URI;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true) // retira o prefixo getsset
public class ApiError {

    private URI type;
    private String title;
    private int status;
    private String detail;
    private URI instance;
    private Map<String, Object> properties;
}