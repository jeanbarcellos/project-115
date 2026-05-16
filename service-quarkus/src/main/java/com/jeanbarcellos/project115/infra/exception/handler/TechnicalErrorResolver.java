package com.jeanbarcellos.project115.infra.exception.handler;

import com.jeanbarcellos.core.error.TechnicalErrorType;

/**
 * Utilitário responsável por traduzir exceções técnicas (nativas, de frameworks ou bibliotecas)
 * para o domínio padronizado de erros da aplicação ({@link TechnicalErrorType}).
 *
 * <p>
 * <b>Contexto Quarkus / Jakarta EE:</b><br>
 * Este resolver é focado nas exceções lançadas pelo JAX-RS (RESTEasy), JPA (Hibernate),
 * Bean Validation e extensões nativas do ecossistema Quarkus.
 * </p>
 *
 * <p>
 * <b>Estratégia de Desacoplamento (Bibliotecas Opcionais):</b><br>
 * Como esta classe pertence a uma biblioteca genérica (core/commons), ela evita importar
 * dependências pesadas e opcionais (ex: JWT, Redis, Fault Tolerance).
 * Para capturar exceções dessas bibliotecas sem causar erros de compilação ({@code ClassNotFound}),
 * utilizamos reflexão ({@link #isInstanceOf(Class, String)}) para verificar a árvore de herança.
 * </p>
 *
 * @author Jean Barcellos
 */
public class TechnicalErrorResolver {

    /**
     * Construtor privado para ocultar o construtor público implícito,
     * garantindo que esta classe utilitária não seja instanciada.
     */
    private TechnicalErrorResolver() {
    }

    /**
     * Analisa uma exceção ({@link Throwable}) e determina qual é o {@link TechnicalErrorType}
     * mais adequado correspondente.
     * <p>
     * A ordem de verificação segue o agrupamento de categorias definidas no enum.
     * </p>
     *
     * @param ex a exceção capturada que precisa ser traduzida.
     * @return o {@link TechnicalErrorType} correspondente ou {@code INTERNAL_ERROR} se não houver mapeamento.
     */
    public static TechnicalErrorType resolveType(Throwable ex) {

        if (ex == null) {
            return TechnicalErrorType.INTERNAL_ERROR;
        }

        // ============================
        // GENERIC
        // ============================

        if (ex instanceof java.net.SocketTimeoutException || ex instanceof java.net.http.HttpTimeoutException) {
            return TechnicalErrorType.TIMEOUT;
        }
        // MicroProfile Fault Tolerance (Timeout)
        if (isInstanceOf(ex.getClass(), "org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException")) {
            return TechnicalErrorType.TIMEOUT;
        }
        // MicroProfile Fault Tolerance (Circuit Breaker Open)
        if (isInstanceOf(ex.getClass(), "org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException")) {
            return TechnicalErrorType.SERVICE_UNAVAILABLE;
        }

        // ============================
        // VALIDATION
        // ============================

        // Erro de Bean Validation (Ex: @NotNull, @Email no corpo ou parâmetros)
        if (ex instanceof jakarta.validation.ConstraintViolationException) {
            return TechnicalErrorType.INPUT_VALIDATION_ERROR;
        }
        if (ex instanceof jakarta.ws.rs.BadRequestException) {
            return TechnicalErrorType.INVALID_PARAMETER;
        }
        if (ex instanceof jakarta.ws.rs.NotSupportedException) {
            return TechnicalErrorType.INVALID_FORMAT;
        }

        // ============================
        // RESOURCE
        // ============================

        if (ex instanceof jakarta.ws.rs.NotFoundException) {
            return TechnicalErrorType.RESOURCE_NOT_FOUND;
        }
        if (isInstanceOf(ex.getClass(), "jakarta.persistence.EntityNotFoundException")){
        // if (ex instanceof jakarta.persistence.EntityNotFoundException) {
            return TechnicalErrorType.RESOURCE_NOT_FOUND;
        }
        if (ex instanceof com.fasterxml.jackson.core.JsonParseException) {
            return TechnicalErrorType.MALFORMED_JSON;
        }

        // ============================
        // CONFLICT / CONCURRENCY
        // ============================

        if (isInstanceOf(ex.getClass(), "jakarta.persistence.OptimisticLockException")){
            return TechnicalErrorType.OPTIMISTIC_LOCK_ERROR;
        }
        if (isInstanceOf(ex.getClass(), "jakarta.persistence.PessimisticLockException")){
            return TechnicalErrorType.PESSIMISTIC_LOCK_ERROR;
        }

        // ============================
        // AUTH / SECURITY
        // ============================

        // Quarkus Security (Nativo) e JAX-RS
        if (isInstanceOf(ex.getClass(), "io.quarkus.security.UnauthorizedException") ||
            ex instanceof jakarta.ws.rs.NotAuthorizedException) {
            return TechnicalErrorType.UNAUTHORIZED;
        }
        if (isInstanceOf(ex.getClass(), "io.quarkus.security.ForbiddenException") ||
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

        // ============================
        // INTEGRATION / EXTERNAL
        // ============================

        // Caso o projeto utilize Feign Client ao invés do MicroProfile Rest Client
        if (isInstanceOf(ex.getClass(), "feign.FeignException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }
        if (isInstanceOf(ex.getClass(), "feign.RetryableException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_UNAVAILABLE;
        }

        // ============================
        // DATA / INFRASTRUCTURE
        // ============================

        if (ex instanceof java.sql.SQLException) {
            return TechnicalErrorType.DATABASE_ERROR;
        }
        // Violação de constraint de banco de dados (Unique Key, Foreign Key) do Hibernate
        if (isInstanceOf(ex.getClass(), "org.hibernate.exception.ConstraintViolationException")) {
            return TechnicalErrorType.DATA_INTEGRITY_VIOLATION;
        }
        if (ex instanceof com.fasterxml.jackson.databind.exc.MismatchedInputException ||
            ex instanceof com.fasterxml.jackson.databind.JsonMappingException) {
            return TechnicalErrorType.DESERIALIZATION_ERROR;
        }
        if (ex instanceof java.net.ConnectException) {
            return TechnicalErrorType.CONNECTION_ERROR;
        }

        // ============================
        // RATE LIMIT / THROTTLING
        // ============================

        // Resilience4j (se utilizado no projeto Quarkus)
        if (isInstanceOf(ex.getClass(), "io.github.resilience4j.ratelimiter.RequestNotPermitted")) {
            return TechnicalErrorType.RATE_LIMIT_EXCEEDED;
        }

        // ============================
        // CACHE
        // ============================

        // Quarkus Redis Client
        if (isInstanceOf(ex.getClass(), "io.quarkus.redis.client.RedisException")) {
            return TechnicalErrorType.CACHE_ERROR;
        }

        // ============================
        // JAX-RS STATUS EXCEPTIONS (Fallback Genérico)
        // ============================
        // Captura exceções HTTP genéricas do RESTEasy (ex: WebApplicationException)
        if (ex instanceof jakarta.ws.rs.WebApplicationException webAppEx) {
            int status = webAppEx.getResponse().getStatus();
            if (status == 404) return TechnicalErrorType.RESOURCE_NOT_FOUND;
            if (status == 400) return TechnicalErrorType.INVALID_PARAMETER;
            if (status == 401) return TechnicalErrorType.UNAUTHORIZED;
            if (status == 403) return TechnicalErrorType.FORBIDDEN;
            if (status == 409) return TechnicalErrorType.CONFLICT;
            if (status == 429) return TechnicalErrorType.RATE_LIMIT_EXCEEDED;
        }

        // Se nenhuma verificação for atendida, retorna erro genérico 500
        return TechnicalErrorType.INTERNAL_ERROR;
    }

    /**
     * Verifica recursivamente se a classe fornecida ou alguma de suas superclasses
     * corresponde ao nome totalmente qualificado informado.
     * <p>
     * Este método age como um operador {@code instanceof} dinâmico, permitindo avaliar
     * hierarquias de erro de bibliotecas que não estão presentes no classpath do projeto,
     * de forma totalmente segura.
     * </p>
     *
     * @param clazz           a classe da exceção a ser avaliada (obtida via {@code ex.getClass()}).
     * @param targetClassName o nome completo da classe base (ex: {@code "io.quarkus.redis.client.RedisException"}).
     * @return {@code true} se a classe for do tipo ou herdar do tipo informado; {@code false} caso contrário.
     */
    private static boolean isInstanceOf(Class<?> clazz, String targetClassName) {
        if (clazz == null) {
            return false;
        }
        if (clazz.getName().equals(targetClassName)) {
            return true;
        }
        // Chamada recursiva para verificar a classe pai (suporta herança completa de exceções)
        return isInstanceOf(clazz.getSuperclass(), targetClassName);
    }

}