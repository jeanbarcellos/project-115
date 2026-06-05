package com.jeanbarcellos.core.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;

public class CommonRateLimitErrorResolver
        extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        if (isInstanceOf(ex.getClass(), "io.github.resilience4j.ratelimiter.RequestNotPermitted")) {
            return TechnicalErrorType.RATE_LIMIT_EXCEEDED;
        }

        return null;
    }

}