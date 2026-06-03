package com.jeanbarcellos.core.quarkus.infra.error;

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
    private TechnicalErrorResolver() { }

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

        // ==============================================================================
        // VALIDATION
        // ==============================================================================
        // #region

        // Erro de Bean Validation (Ex: @NotNull, @Email no corpo ou parâmetros)
        if (ex instanceof jakarta.validation.ConstraintViolationException) {
            return TechnicalErrorType.INPUT_VALIDATION_ERROR;
        }

        if (ex instanceof jakarta.validation.ValidationException) {
            return TechnicalErrorType.SYSTEM_VALIDATION_ERROR;
        }
        if (ex instanceof jakarta.ws.rs.BadRequestException) {
            return TechnicalErrorType.INVALID_PARAMETER;
        }

        if (hasCause(ex,
                "org.jboss.resteasy.reactive.server.validation.ResteasyReactiveViolationException")) {
            return TechnicalErrorType.INPUT_VALIDATION_ERROR;
        }

        // #endregion

        // ==============================================================================
        // RESOURCE
        // ==============================================================================
        // #region

        if (ex instanceof jakarta.ws.rs.NotFoundException) {
            return TechnicalErrorType.RESOURCE_NOT_FOUND;
        }

        if (ex instanceof jakarta.ws.rs.NotAllowedException) {
            return TechnicalErrorType.RESOURCE_METHOD_NOT_ALLOWED;
        }

        if (ex instanceof jakarta.ws.rs.NotSupportedException) {
            return TechnicalErrorType.RESOURCE_UNSUPPORTED_MEDIA_TYPE;
        }

        if (isInstanceOf(ex.getClass(), "jakarta.persistence.EntityNotFoundException")){
            return TechnicalErrorType.RESOURCE_NOT_FOUND;
        }

        if (ex instanceof com.fasterxml.jackson.core.JsonParseException) {
            return TechnicalErrorType.MALFORMED_JSON;
        }

        // #endregion

        // ==============================================================================
        // AUTH / SECURITY
        // ==============================================================================
        // #region

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

        // #endregion

        // ==============================================================================
        // CONFLICT / CONCURRENCY
        // ==============================================================================
        // #region

        if (isInstanceOf(ex.getClass(), "jakarta.persistence.OptimisticLockException")){
            return TechnicalErrorType.OPTIMISTIC_LOCK_ERROR;
        }
        if (isInstanceOf(ex.getClass(), "jakarta.persistence.PessimisticLockException")){
            return TechnicalErrorType.PESSIMISTIC_LOCK_ERROR;
        }

        // #endregion

        // ==============================================================================
        // DATA
        // ==============================================================================
        // #region

        if (ex instanceof java.sql.SQLException) {
            return TechnicalErrorType.DATABASE_ERROR;
        }

        if (ex instanceof java.sql.SQLTimeoutException) {
            return TechnicalErrorType.TIMEOUT;
        }

        if (isInstanceOf(ex.getClass(), "jakarta.persistence.QueryTimeoutException")) {
            return TechnicalErrorType.TIMEOUT;
        }

        if (hasCause(ex, "org.hibernate.QueryTimeoutException")) {
            return TechnicalErrorType.TIMEOUT;
        }

        // Violação de constraint de banco de dados (Unique Key, Foreign Key) do Hibernate
        if (isInstanceOf(ex.getClass(), "org.hibernate.exception.ConstraintViolationException")) {
            return TechnicalErrorType.DATA_INTEGRITY_VIOLATION;
        }

        if (ex instanceof com.fasterxml.jackson.databind.exc.MismatchedInputException
                || ex instanceof com.fasterxml.jackson.databind.JsonMappingException) {
            return TechnicalErrorType.DESERIALIZATION_ERROR;
        }

        // #endregion

        // ==============================================================================
        // INTEGRATION / EXTERNAL
        // ==============================================================================
        // #region

        if (hasCause(ex, "java.net.SocketTimeoutException")
                || hasCause(ex, "java.net.http.HttpTimeoutException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_TIMEOUT;
        }

        if (hasCause(ex, "java.net.SocketTimeoutException")
                || hasCause(ex, "java.net.http.HttpTimeoutException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_TIMEOUT;
        }

        if (hasCause(ex, "org.eclipse.microprofile.rest.client.RestClientException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }


        if (hasCause(ex, "org.jboss.resteasy.reactive.ClientWebApplicationException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }


        if (hasCause(ex, "feign.codec.DecodeException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_BAD_RESPONSE;
        }


        if (hasCause(ex, "com.fasterxml.jackson.databind.JsonMappingException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_BAD_RESPONSE;
        }

        // Caso o projeto utilize Feign Client ao invés do MicroProfile Rest Client
        if (isInstanceOf(ex.getClass(), "feign.FeignException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }

        if (isInstanceOf(ex.getClass(), "feign.RetryableException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_UNAVAILABLE;
        }

        // #endregion

        // ==============================================================================
        // INFRASTRUCTURE
        // ==============================================================================
        // #region

        if (ex instanceof java.net.ConnectException) {
            return TechnicalErrorType.CONNECTION_ERROR;
        }

        if (ex instanceof java.net.UnknownHostException) {
            return TechnicalErrorType.CONNECTION_ERROR;
        }

        if (ex instanceof java.net.SocketException) {
            return TechnicalErrorType.CONNECTION_ERROR;
        }

        if (ex instanceof java.io.FileNotFoundException) {
            return TechnicalErrorType.FILE_STORAGE_ERROR;
        }

        if (ex instanceof java.nio.file.AccessDeniedException) {
            return TechnicalErrorType.ACCESS_DENIED;
        }

        if (ex instanceof java.nio.file.FileSystemException) {
            return TechnicalErrorType.FILE_STORAGE_ERROR;
        }

        if (hasCause(ex, "org.apache.kafka.common.KafkaException")) {
            return TechnicalErrorType.MESSAGE_BROKER_ERROR;
        }

        if (hasCause(ex, "org.apache.kafka.common.errors.TimeoutException")) {
            return TechnicalErrorType.MESSAGE_BROKER_ERROR;
        }

        if (hasCause(ex, "org.springframework.amqp.AmqpException")) {
            return TechnicalErrorType.MESSAGE_BROKER_ERROR;
        }

        // #endregion

        // ==============================================================================
        // RATE LIMIT / THROTTLING
        // ==============================================================================
        // #region

        // Resilience4j (se utilizado no projeto Quarkus)
        if (isInstanceOf(ex.getClass(), "io.github.resilience4j.ratelimiter.RequestNotPermitted")) {
            return TechnicalErrorType.RATE_LIMIT_EXCEEDED;
        }

        // #endregion

        // ==============================================================================
        // CACHE
        // ==============================================================================
        // #region

        // Quarkus Redis Client
        if (isInstanceOf(ex.getClass(), "io.quarkus.redis.client.RedisException")) {
            return TechnicalErrorType.CACHE_ERROR;
        }

        // #endregion

        // ==============================================================================
        // GENERIC
        // ==============================================================================
        // #region

        // MicroProfile Fault Tolerance (Timeout)
        if (isInstanceOf(ex.getClass(), "org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException")) {
            return TechnicalErrorType.TIMEOUT;
        }

        // MicroProfile Fault Tolerance (Circuit Breaker Open)
        if (isInstanceOf(ex.getClass(), "org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException")) {
            return TechnicalErrorType.SERVICE_UNAVAILABLE;
        }

        // #endregion

        // ==============================================================================
        // JAX-RS STATUS EXCEPTIONS (Fallback Genérico)
        // ==============================================================================
        // #region

        // Captura exceções HTTP genéricas do RESTEasy (ex: WebApplicationException)
        if (ex instanceof jakarta.ws.rs.WebApplicationException webAppEx) {

            int status = webAppEx.getResponse().getStatus();

            if (status == 404) return TechnicalErrorType.RESOURCE_NOT_FOUND;
            if (status == 400) return TechnicalErrorType.INVALID_PARAMETER;
            if (status == 401) return TechnicalErrorType.UNAUTHORIZED;
            if (status == 403) return TechnicalErrorType.FORBIDDEN;
            if (status == 405) return TechnicalErrorType.RESOURCE_METHOD_NOT_ALLOWED;
            if (status == 409) return TechnicalErrorType.CONFLICT;
            if (status == 415) return TechnicalErrorType.RESOURCE_UNSUPPORTED_MEDIA_TYPE;
            if (status == 429) return TechnicalErrorType.RATE_LIMIT_EXCEEDED;
        }

        // #endregion

        // Se nenhuma verificação for atendida, retorna erro genérico 500
        return TechnicalErrorType.INTERNAL_ERROR;
    }

    /**
     * Verifica recursivamente se a classe fornecida ou alguma de suas superclasses corresponde ao
     * nome totalmente qualificado informado.
     * <p>
     * Este método age como um operador {@code instanceof} dinâmico, permitindo avaliar hierarquias
     * de erro de bibliotecas que não estão presentes no classpath do projeto, de forma totalmente
     * segura.
     * </p>
     *
     * @param clazz a classe da exceção a ser avaliada (obtida via {@code ex.getClass()}).
     * @param targetClassName o nome completo da classe base (ex:
     *        {@code "io.quarkus.redis.client.RedisException"}).
     * @return {@code true} se a classe for do tipo ou herdar do tipo informado; {@code false} caso
     *         contrário.
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

    /**
     * Verifica se a exceção ou qualquer uma de suas causas pertence à hierarquia de um tipo
     * informado.
     *
     * @param throwable exceção inicial.
     * @param targetClassName nome totalmente qualificado do tipo procurado.
     * @return true quando encontrado na cadeia de causas.
     */
    private static boolean hasCause(Throwable throwable, String targetClassName) {

        while (throwable != null) {

            if (isInstanceOf(throwable.getClass(), targetClassName)) {
                return true;
            }

            throwable = throwable.getCause();
        }

        return false;
    }

}
