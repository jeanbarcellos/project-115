package com.jeanbarcellos.core.observability.spring.generator;

import com.jeanbarcellos.core.observability.generator.CorrelationIdGenerator;

import java.util.UUID;

/**
 * Implementação padrão de {@link CorrelationIdGenerator} baseada em UUID.
 *
 * <p>
 * Esta implementação é registrada automaticamente pela biblioteca, podendo ser
 * sobrescrita por qualquer microsserviço através de um bean próprio.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
public class UuidCorrelationIdGenerator implements CorrelationIdGenerator {

    /**
     * {@inheritDoc}
     */
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }

}