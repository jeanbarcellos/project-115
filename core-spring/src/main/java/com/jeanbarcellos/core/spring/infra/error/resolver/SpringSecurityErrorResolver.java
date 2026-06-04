package com.jeanbarcellos.core.spring.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public class SpringSecurityErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {
        // Spring Security
        if (isInstanceOf(ex.getClass(), "org.springframework.security.core.AuthenticationException")) {
            return TechnicalErrorType.UNAUTHORIZED;
        }
        if (isInstanceOf(ex.getClass(), "org.springframework.security.access.AccessDeniedException")) {
            return TechnicalErrorType.FORBIDDEN;
        }
        // Bibliotecas de JWT (JJWT & Auth0)
        if (isInstanceOf(ex.getClass(), "io.jsonwebtoken.ExpiredJwtException") ||
                isInstanceOf(ex.getClass(), "com.auth0.jwt.exceptions.TokenExpiredException")) {
            return TechnicalErrorType.TOKEN_EXPIRED;
        }
        if (isInstanceOf(ex.getClass(), "io.jsonwebtoken.JwtException") ||
                isInstanceOf(ex.getClass(), "com.auth0.jwt.exceptions.JWTVerificationException")) {
            return TechnicalErrorType.INVALID_TOKEN;
        }

        return null;
    }

}
