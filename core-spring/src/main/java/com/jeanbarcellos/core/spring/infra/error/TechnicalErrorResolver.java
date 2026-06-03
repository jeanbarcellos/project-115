package com.jeanbarcellos.core.spring.infra.error;

import com.jeanbarcellos.core.error.TechnicalErrorType;

/**
 * Utilitário responsável por traduzir exceções técnicas (nativas, de frameworks ou bibliotecas)
 * para o domínio padronizado de erros da aplicação ({@link TechnicalErrorType}).
 *
 * <p>
 * <b>Estratégia de Desacoplamento (Bibliotecas Opcionais):</b><br>
 * Como esta classe pertence a uma biblioteca genérica (core/commons), ela evita importar
 * dependências pesadas e opcionais (ex: Spring Security, OpenFeign, Resilience4j, JJWT). Para
 * capturar exceções dessas bibliotecas sem causar erros de compilação ({@code ClassNotFound}),
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

        // ==============================================================================
        // VALIDATION
        // ==============================================================================
        // #region

        if (ex instanceof org.springframework.web.bind.MethodArgumentNotValidException) {
            return TechnicalErrorType.INPUT_VALIDATION_ERROR;
        }

        if (isInstanceOf(ex.getClass(),
                "org.springframework.validation.method.MethodValidationException")) {
            return TechnicalErrorType.INPUT_VALIDATION_ERROR;
        }

        if (isInstanceOf(ex.getClass(),
                "org.springframework.web.method.annotation.HandlerMethodValidationException")) {
            return TechnicalErrorType.INPUT_VALIDATION_ERROR;
        }

        if (ex instanceof jakarta.validation.ConstraintViolationException) {
            return TechnicalErrorType.INPUT_VALIDATION_ERROR;
        }

        if (ex instanceof jakarta.validation.ValidationException) {
            return TechnicalErrorType.SYSTEM_VALIDATION_ERROR;
        }

        if (ex instanceof org.springframework.validation.BindException) {
            return TechnicalErrorType.INPUT_VALIDATION_ERROR;
        }

        if (ex instanceof org.springframework.web.method.annotation.MethodArgumentTypeMismatchException) {
            return TechnicalErrorType.INVALID_PARAMETER;
        }

        if (ex instanceof org.springframework.beans.TypeMismatchException) {
            return TechnicalErrorType.INVALID_PARAMETER;
        }

        if (ex instanceof org.springframework.core.convert.ConversionFailedException) {
            return TechnicalErrorType.INVALID_PARAMETER;
        }

        if (ex instanceof org.springframework.beans.ConversionNotSupportedException) {
            return TechnicalErrorType.INVALID_PARAMETER;
        }

        if (ex instanceof org.springframework.web.bind.MissingServletRequestParameterException) {
            return TechnicalErrorType.MISSING_PARAMETER;
        }

        if (ex instanceof org.springframework.web.multipart.support.MissingServletRequestPartException) {
            return TechnicalErrorType.MISSING_PARAMETER;
        }

        if (ex instanceof org.springframework.web.bind.MissingPathVariableException) {
            return TechnicalErrorType.MISSING_PARAMETER;
        }

        if (ex instanceof org.springframework.web.bind.MissingRequestHeaderException) {
            return TechnicalErrorType.MISSING_PARAMETER;
        }

        if (ex instanceof org.springframework.web.bind.MissingRequestCookieException) {
            return TechnicalErrorType.MISSING_PARAMETER;
        }

        if (ex instanceof org.springframework.web.bind.ServletRequestBindingException) {
            return TechnicalErrorType.MISSING_PARAMETER;
        }

        // #endregion

        // ==============================================================================
        // RESOURCE
        // ==============================================================================
        // #region

        if (ex instanceof org.springframework.web.HttpRequestMethodNotSupportedException) {
            return TechnicalErrorType.RESOURCE_METHOD_NOT_ALLOWED;
        }

        if (ex instanceof org.springframework.web.HttpMediaTypeNotSupportedException) {
            return TechnicalErrorType.RESOURCE_UNSUPPORTED_MEDIA_TYPE;
        }

        if (ex instanceof org.springframework.web.HttpMediaTypeNotAcceptableException) {
            return TechnicalErrorType.INVALID_FORMAT;
        }

        if (isInstanceOf(ex.getClass(),
                "org.springframework.web.servlet.NoHandlerFoundException")) {
            return TechnicalErrorType.RESOURCE_NOT_FOUND;
        }

        if (ex instanceof jakarta.persistence.EntityNotFoundException) {
            return TechnicalErrorType.RESOURCE_NOT_FOUND;
        }

        if (ex instanceof org.springframework.dao.EmptyResultDataAccessException) {
            return TechnicalErrorType.RESOURCE_NOT_FOUND;
        }

        if (ex instanceof org.springframework.http.converter.HttpMessageNotReadableException) {
            return TechnicalErrorType.MALFORMED_JSON;
        }

        if (ex instanceof com.fasterxml.jackson.core.JsonParseException) {
            return TechnicalErrorType.MALFORMED_JSON;
        }

        // #endregion

        // ==============================================================================
        // AUTH / SECURITY
        // ==============================================================================
        // #region

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

        // #endregion

        // ==============================================================================
        // CONFLICT / CONCURRENCY
        // ==============================================================================
        // #region

        if (ex instanceof org.springframework.dao.OptimisticLockingFailureException) {
            return TechnicalErrorType.OPTIMISTIC_LOCK_ERROR;
        }

        if (ex instanceof org.springframework.dao.PessimisticLockingFailureException) {
            return TechnicalErrorType.PESSIMISTIC_LOCK_ERROR;
        }

        if (ex instanceof org.springframework.dao.CannotAcquireLockException) {
            return TechnicalErrorType.PESSIMISTIC_LOCK_ERROR;
        }

        if (ex instanceof org.springframework.dao.DeadlockLoserDataAccessException) {
            return TechnicalErrorType.PESSIMISTIC_LOCK_ERROR;
        }

        // #endregion

        // ==============================================================================
        // DATA
        // ==============================================================================
        // #region

        if (ex instanceof java.sql.SQLTimeoutException) {
            return TechnicalErrorType.TIMEOUT;
        }

        if (ex instanceof jakarta.persistence.QueryTimeoutException) {
            return TechnicalErrorType.TIMEOUT;
        }

        if (ex instanceof org.springframework.web.context.request.async.AsyncRequestTimeoutException) {
            return TechnicalErrorType.TIMEOUT;
        }

        if (ex instanceof org.springframework.transaction.TransactionException) {
            return TechnicalErrorType.DATABASE_ERROR;
        }

        if (ex instanceof java.sql.SQLException) {
            return TechnicalErrorType.DATABASE_ERROR;
        }

        if (ex instanceof org.springframework.dao.DataIntegrityViolationException) {
            return TechnicalErrorType.DATA_INTEGRITY_VIOLATION;
        }

        if (ex instanceof com.fasterxml.jackson.databind.exc.MismatchedInputException) {
            return TechnicalErrorType.DESERIALIZATION_ERROR;
        }

        if (ex instanceof org.springframework.http.converter.HttpMessageNotWritableException) {
            return TechnicalErrorType.SERIALIZATION_ERROR;
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

        // OpenFeign Clients

        if (hasCause(ex, "feign.codec.DecodeException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_BAD_RESPONSE;
        }

        if (hasCause(ex, "com.fasterxml.jackson.databind.JsonMappingException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_BAD_RESPONSE;
        }

        if (hasCause(ex, "org.springframework.core.codec.DecodingException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_BAD_RESPONSE;
        }

        if (hasCause(ex, "feign.RetryableException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_UNAVAILABLE;
        }

        if (hasCause(ex, "feign.FeignException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }

        // RestTemplate

        if (hasCause(ex, "org.springframework.web.client.UnknownHttpStatusCodeException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_BAD_RESPONSE;
        }

        if (hasCause(ex, "org.springframework.web.client.HttpStatusCodeException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }
        if (hasCause(ex, "org.springframework.web.client.ResourceAccessException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }

        // WebClient

        if (hasCause(ex,
                "org.springframework.web.reactive.function.client.WebClientResponseException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }

        if (hasCause(ex,
                "org.springframework.web.reactive.function.client.WebClientRequestException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_ERROR;
        }

        // Apache HttpClient

        if (isInstanceOf(ex.getClass(),
                "org.apache.hc.client5.http.ConnectTimeoutException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_TIMEOUT;
        }

        // Outros

        if (hasCause(ex, "javax.net.ssl.SSLException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_UNAVAILABLE;
        }

        if (hasCause(ex, "java.net.ConnectException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_UNAVAILABLE;
        }

        if (hasCause(ex, "java.net.UnknownHostException")) {
            return TechnicalErrorType.EXTERNAL_SERVICE_UNAVAILABLE;
        }

        // #endregion

        // ==============================================================================
        // INFRASTRUCTURE
        // ==============================================================================
        // #region

        if (ex instanceof org.springframework.beans.factory.BeanCreationException) {
            return TechnicalErrorType.CONFIGURATION_ERROR;
        }

        if (ex instanceof org.springframework.beans.factory.BeanDefinitionStoreException) {
            return TechnicalErrorType.CONFIGURATION_ERROR;
        }

        if (ex instanceof org.springframework.dao.DataAccessResourceFailureException) {
            return TechnicalErrorType.CONNECTION_ERROR;
        }

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

        if (hasCause(ex, "org.springframework.amqp.AmqpException")) {
            return TechnicalErrorType.MESSAGE_BROKER_ERROR;
        }

        // #endregion

        // ==============================================================================
        // RATE LIMIT / THROTTLING
        // ==============================================================================
        // #region

        if (isInstanceOf(ex.getClass(), "io.github.resilience4j.ratelimiter.RequestNotPermitted")) {
            return TechnicalErrorType.RATE_LIMIT_EXCEEDED;
        }

        // #endregion

        // ==============================================================================
        // CACHE
        // ==============================================================================
        // #region

        if (isInstanceOf(ex.getClass(), "org.springframework.data.redis.RedisConnectionFailureException")) {
            return TechnicalErrorType.CACHE_ERROR;
        }
        if (isInstanceOf(ex.getClass(), "org.springframework.cache.Cache$ValueRetrievalException")) {
            return TechnicalErrorType.CACHE_ERROR;
        }

        // #endregion

        // ==============================================================================
        // SPRING STATUS EXCEPTIONS (Fallback)
        // ==============================================================================
        // #region

        // Captura exceções genéricas lançadas manualmente como throw new ResponseStatusException(...)
        if (ex instanceof org.springframework.web.server.ResponseStatusException responseStatusEx) {

            int status = responseStatusEx.getStatusCode().value();

            if (status == 400) return TechnicalErrorType.INVALID_PARAMETER;
            if (status == 401) return TechnicalErrorType.UNAUTHORIZED;
            if (status == 403) return TechnicalErrorType.FORBIDDEN;
            if (status == 404) return TechnicalErrorType.RESOURCE_NOT_FOUND;
            if (status == 405) return TechnicalErrorType.RESOURCE_METHOD_NOT_ALLOWED;
            if (status == 409) return TechnicalErrorType.CONFLICT;
            if (status == 415) return TechnicalErrorType.RESOURCE_UNSUPPORTED_MEDIA_TYPE;
            if (status == 429) return TechnicalErrorType.RATE_LIMIT_EXCEEDED;
        }

        // #endregion

        // ==============================================================================
        // GENERIC
        // ==============================================================================
        // #region

        if (isInstanceOf(ex.getClass(), "io.github.resilience4j.circuitbreaker.CallNotPermittedException")) {
            return TechnicalErrorType.SERVICE_UNAVAILABLE;
        }

        // #endregion

        // Se nenhuma verificação for atendida, retorna erro genérico 500
        return TechnicalErrorType.INTERNAL_ERROR;
    }

    /**
     * Verifica se uma classe pertence à hierarquia de uma determinada classe
     * informada pelo nome totalmente qualificado.
     *
     * <p>
     * O algoritmo percorre:
     * </p>
     *
     * <ul>
     * <li>A própria classe;</li>
     * <li>Suas interfaces;</li>
     * <li>Toda a cadeia de superclasses.</li>
     * </ul>
     *
     * <p>
     * Esta abordagem permite identificar tipos de bibliotecas opcionais
     * sem criar dependências de compilação diretas.
     * </p>
     *
     * @param clazz           classe a ser analisada.
     * @param targetClassName nome totalmente qualificado do tipo esperado.
     * @return {@code true} quando o tipo for encontrado na hierarquia.
     */
    private static boolean isInstanceOf(Class<?> clazz, String targetClassName) {

        while (clazz != null) {
            if (clazz.getName().equals(targetClassName)) {
                return true;
            }

            for (Class<?> iface : clazz.getInterfaces()) {
                if (isInstanceOf(iface, targetClassName)) {
                    return true;
                }
            }

            clazz = clazz.getSuperclass();
        }

        return false;
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