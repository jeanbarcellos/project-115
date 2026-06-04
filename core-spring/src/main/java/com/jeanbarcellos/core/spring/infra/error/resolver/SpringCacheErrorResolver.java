package com.jeanbarcellos.core.spring.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public class SpringCacheErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        if (isInstanceOf(ex.getClass(), "org.springframework.data.redis.RedisConnectionFailureException")) {
            return TechnicalErrorType.CACHE_ERROR;
        }
        
        if (isInstanceOf(ex.getClass(), "org.springframework.cache.Cache$ValueRetrievalException")) {
            return TechnicalErrorType.CACHE_ERROR;
        }

        return null;
    }

}
