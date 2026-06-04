package com.jeanbarcellos.core.quarkus.infra.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.resolver.AbstractErrorResolver;

public class QuarkusSecurityErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        // Quarkus Security (Nativo) e JAX-RS
        if (hasCause(ex, "io.quarkus.security.UnauthorizedException") ||
                ex instanceof jakarta.ws.rs.NotAuthorizedException) {
            return TechnicalErrorType.UNAUTHORIZED;
        }
        if (hasCause(ex, "io.quarkus.security.ForbiddenException") ||
                ex instanceof jakarta.ws.rs.ForbiddenException) {
            return TechnicalErrorType.FORBIDDEN;
        }
        // SmallRye JWT (Padrão do Quarkus)
        if (isInstanceOf(ex.getClass(), "io.smallrye.jwt.build.JwtException")) {
            return TechnicalErrorType.INVALID_TOKEN;
        }
        // Bibliotecas de JWT (JJWT & Auth0) - Caso a aplicação não use SmallRye
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
