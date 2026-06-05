package com.jeanbarcellos.core.error.resolver;

import com.jeanbarcellos.core.error.TechnicalErrorType;

/**
 * Resolver responsável por erros de autenticação
 * e autorização independentes de framework.
 *
 * @author Jean Barcellos
 */
public class CommonSecurityErrorResolver extends AbstractErrorResolver {

    @Override
    public TechnicalErrorType resolve(Throwable ex) {

        if (isInstanceOf(ex.getClass(), "io.jsonwebtoken.ExpiredJwtException")) {
            return TechnicalErrorType.TOKEN_EXPIRED;
        }

        if (isInstanceOf(ex.getClass(), "com.auth0.jwt.exceptions.TokenExpiredException")) {
            return TechnicalErrorType.TOKEN_EXPIRED;
        }

        if (isInstanceOf(ex.getClass(), "io.jsonwebtoken.JwtException")) {
            return TechnicalErrorType.INVALID_TOKEN;
        }

        if (isInstanceOf(ex.getClass(), "com.auth0.jwt.exceptions.JWTVerificationException")) {
            return TechnicalErrorType.INVALID_TOKEN;
        }

        return null;
    }

}