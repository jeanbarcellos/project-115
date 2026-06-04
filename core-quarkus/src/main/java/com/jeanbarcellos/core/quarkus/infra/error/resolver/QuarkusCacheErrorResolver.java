package com.jeanbarcellos.core.quarkus.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public class QuarkusCacheErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        // Quarkus Redis Client
        if (isInstanceOf(ex.getClass(), "io.quarkus.redis.client.RedisException")) {
            return TechnicalErrorType.CACHE_ERROR;
        }

        return null;
    }

}
