package com.jeanbarcellos.core.spring.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public class SpringGenericErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {


        if (isInstanceOf(ex.getClass(), "io.github.resilience4j.circuitbreaker.CallNotPermittedException")) {
            return TechnicalErrorType.SERVICE_UNAVAILABLE;
        }

        return null;
    }

}
