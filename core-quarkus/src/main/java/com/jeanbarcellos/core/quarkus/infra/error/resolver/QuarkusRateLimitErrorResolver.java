package com.jeanbarcellos.core.quarkus.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public class QuarkusRateLimitErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        // Resilience4j (se utilizado no projeto Quarkus)
        if (isInstanceOf(ex.getClass(), "io.github.resilience4j.ratelimiter.RequestNotPermitted")) {
            return TechnicalErrorType.RATE_LIMIT_EXCEEDED;
        }

        return null;
    }

}