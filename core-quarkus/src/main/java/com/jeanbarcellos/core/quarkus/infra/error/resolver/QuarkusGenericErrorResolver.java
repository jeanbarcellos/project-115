package com.jeanbarcellos.core.quarkus.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public class QuarkusGenericErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        // MicroProfile Fault Tolerance (Timeout)
        if (isInstanceOf(ex.getClass(), "org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException")) {
            return TechnicalErrorType.TIMEOUT;
        }

        // MicroProfile Fault Tolerance (Circuit Breaker Open)
        if (isInstanceOf(ex.getClass(),
                "org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException")) {
            return TechnicalErrorType.SERVICE_UNAVAILABLE;
        }

        return null;
    }

}
