package com.jeanbarcellos.core.observability.spring.generator;

import com.jeanbarcellos.core.observability.generator.CorrelationIdGenerator;

import java.util.UUID;

public class UuidCorrelationIdGenerator
        implements CorrelationIdGenerator {

    @Override
    public String generate() {

        return UUID.randomUUID().toString();

    }

}