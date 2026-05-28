package com.jeanbarcellos.project115.infra.error;

import com.jeanbarcellos.core.error.TechnicalErrorType;

/**
 * Utilitário responsável por traduzir exceções técnicas (nativas, de frameworks ou bibliotecas)
 * para o domínio padronizado de erros da aplicação ({@link TechnicalErrorType}).
 *
 * <p>
 * <b>Estratégia de Desacoplamento (Bibliotecas Opcionais):</b><br>
 * Como esta classe pertence a uma biblioteca genérica (core/commons), ela evita importar
 * dependências pesadas e opcionais (ex: Spring Security, OpenFeign, Resilience4j, JJWT).
 * Para capturar exceções dessas bibliotecas sem causar erros de compilação ({@code ClassNotFound}),
 * utilizamos reflexão ({@link #isInstanceOf(Class, String)}) para verificar a árvore de herança.
 * </p>
 *
 * @author Jean Barcellos
 */
public final class TechnicalErrorResolver {

    /**
     * Construtor privado para ocultar o construtor público implícito,
     * garantindo que esta classe utilitária não seja instanciada.
     */
    private TechnicalErrorResolver() {}

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
        if (isInstanceOf(ex.getClass(), "io.github.resilience4j.circuitbreaker.CallNotPermittedException")) {
            return TechnicalErrorType.SERVICE_UNAVAILABLE;
        }

        // ============================
        // VALIDATION
        // ============================

        if (ex instanceof org.springframework.web.bind.MethodArgumentNotValidException) {
            return TechnicalErrorType.INPUT_VALIDATION_ERROR;
        }
        if (ex instanceof jakarta.validation.ConstraintViolationException) { // Use javax.validation se Spring Boot < 3
            return TechnicalErrorType.INPUT_VALIDATION_ERROR;
        }
        if (ex instanceof org.springframework.validation.BindException) {
            return TechnicalErrorType.INPUT_VALIDATION_ERROR;
        }
        if (ex instanceof org.springframework.web.method.annotation.MethodArgumentTypeMismatchException) {
            return TechnicalErrorType.INVALID_PARAMETER;
        }
        if (ex instanceof org.springframework.web.bind.MissingServletRequestParameterException) {
            return TechnicalErrorType.MISSING_PARAMETER;
        }
        if (ex instanceof org.springframework.web.HttpRequestMethodNotSupportedException) {
            return TechnicalErrorType.INVALID_FORMAT;
        }
        if (ex instanceof org.springframework.web.HttpMediaTypeNotSupportedException) {
            return TechnicalErrorType.INVALID_FORMAT;
        }

        // ============================
        // RESOURCE
        // ============================

        if (ex instanceof org.springframework.web.servlet.NoHandlerFoundException) {
            return TechnicalErrorType.RESOURCE_NOT_FOUND;
        }
        if (ex instanceof jakarta.persistence.EntityNotFoundException) { // JPA
            return TechnicalErrorType.RESOURCE_NOT_FOUND;
        }
        if (ex instanceof org.springframework.dao.EmptyResultDataAccessException) { // Spring Data
            return TechnicalErrorType.RESOURCE_NOT_FOUND;
        }
        if (ex instanceof org.springframework.http.converter.HttpMessageNotReadableException) {
            return TechnicalErrorType.MALFORMED_JSON;
        }
        if (ex instanceof com.fasterxml.jackson.core.JsonParseException) {
            return TechnicalErrorType.MALFORMED_JSON;
        }

        // ============================
        // CONFLICT / CONCURRENCY
        // ============================

        if (ex instanceof org.springframework.dao.OptimisticLockingFailureException) {
            return TechnicalErrorType.OPTIMISTIC_LOCK_ERROR;
        }
        if (ex instanceof org.springframework.dao.PessimisticLockingFailureException) {
            return TechnicalErrorType.PESSIMISTIC_LOCK_ERROR;
        }

        // ============================
        // AUTH / SECURITY
        // ============================

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

        // ============================
        // INTEGRATION / EXTERNAL
        // ============================

        // OpenFeign Clients
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
        if (ex instanceof org.springframework.dao.DataIntegrityViolationException) {
            return TechnicalErrorType.DATA_INTEGRITY_VIOLATION;
        }
        if (ex instanceof com.fasterxml.jackson.databind.exc.MismatchedInputException) {
            return TechnicalErrorType.DESERIALIZATION_ERROR;
        }
        if (ex instanceof java.net.ConnectException) {
            return TechnicalErrorType.CONNECTION_ERROR;
        }

        // ============================
        // RATE LIMIT / THROTTLING
        // ============================

        if (isInstanceOf(ex.getClass(), "io.github.resilience4j.ratelimiter.RequestNotPermitted")) {
            return TechnicalErrorType.RATE_LIMIT_EXCEEDED;
        }

        // ============================
        // CACHE
        // ============================

        if (isInstanceOf(ex.getClass(), "org.springframework.data.redis.RedisConnectionFailureException")) {
            return TechnicalErrorType.CACHE_ERROR;
        }
        if (isInstanceOf(ex.getClass(), "org.springframework.cache.Cache$ValueRetrievalException")) {
            return TechnicalErrorType.CACHE_ERROR;
        }

        // ============================
        // SPRING STATUS EXCEPTIONS (Fallback)
        // ============================
        // Captura exceções genéricas lançadas manualmente como throw new ResponseStatusException(...)
        if (ex instanceof org.springframework.web.server.ResponseStatusException responseStatusEx) {
            int status = responseStatusEx.getStatusCode().value();
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
     * @param targetClassName o nome completo da classe base (ex: {@code "feign.FeignException"}).
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