package com.jeanbarcellos.project115.infra.exception.handler;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.jeanbarcellos.core.error.ErrorCategory;
import com.jeanbarcellos.core.error.ErrorResponse;
import com.jeanbarcellos.core.error.ErrorType;
import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.ValidationError;
import com.jeanbarcellos.core.exception.ApplicationException;
import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.core.exception.DomainValidationException;
import com.jeanbarcellos.core.exception.ValidationException;
import com.jeanbarcellos.core.exception.integration.IntegrationException;
import com.jeanbarcellos.core.observability.CorrelationContext;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    /**
     * Media type padrão da RFC 7807.
     */
    private static final String MEDIA_TYPE_APPLICATION_PROBLEM_JSON = "application/problem+json";

    /**
     * URI base utilizada na construção
     * do campo {@code type} do Problem Details.
     */
    @ConfigProperty(name = "api.problem.base-uri")
    String problemBaseUri;

    /**
     * Indica se erros 4xx devem ser logados.
     */
    private boolean logClientErrors = true;

    /**
     * Informações da URI atual.
     */
    @Context
    UriInfo uriInfo;

    /**
     * Contexto da requisição atual.
     */
    @Context
    ContainerRequestContext requestContext;

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof DomainValidationException ex) {
            return this.handleDomainValidation(ex);
        }

        if (exception instanceof DomainException ex) {
            return this.handleDomainException(ex);
        }

        if (exception instanceof BusinessException ex) {
            return this.handleBusinessException(ex);
        }

        if (exception instanceof ValidationException ex) {
            return this.handleValidationException(ex);
        }

        if (exception instanceof IntegrationException ex) {
            return this.handleIntegrationException(ex);
        }

        if (exception instanceof ApplicationException ex) {
            return this.handleApplicationException(ex);
        }

        if (exception instanceof NotFoundException ex) {
            return this.handleNotFoundException(ex);
        }

        return this.handleTechnicalException(exception);
    }

    // =========================================================================
    // DOMAIN
    // =========================================================================

    /**
     * Fallback para exceções de domínio não tratadas corretamente.
     *
     * @param ex exceção capturada
     * @return resposta RFC 7807
     */
    private Response handleDomainException(DomainException ex) {

        ErrorCategory category = ErrorCategory.DOMAIN;
        ErrorType errorType = TechnicalErrorType.SYSTEM_VALIDATION_ERROR;

        this.log(category, errorType, ex, ex.getMessage());

        return this.buildResponse(errorType, ex.getMessage(), ex.getContext());
    }

    /**
     * Trata erros de validação de domínio.
     *
     * @param ex exceção capturada
     * @return resposta RFC 7807
     */
    private Response handleDomainValidation(DomainValidationException ex) {

        ErrorCategory category = ErrorCategory.VALIDATION;
        ErrorType errorType = TechnicalErrorType.INPUT_VALIDATION_ERROR;

        List<ValidationError> errors = ex.getViolations()
                .stream()
                .map(violation -> ValidationError.of(
                        violation.getField(),
                        violation.getMessage(),
                        violation.getRejectedValue()))
                .toList();

        this.log(category, errorType, ex, ex.getMessage());

        return this.buildResponse(errorType, ex.getMessage(), errors);
    }

    // =========================================================================
    // BUSINESS
    // =========================================================================

    /**
     * Trata exceções de negócio conhecidas.
     *
     * @param ex      exceção capturada
     * @param request requisição HTTP atual
     * @return resposta RFC 7807
     */
    private Response handleBusinessException(BusinessException ex) {

        ErrorCategory category = ErrorCategory.BUSINESS;
        ErrorType errorType = ex.getType();

        this.log(category, errorType, ex, ex.getMessage());

        return this.buildResponse(errorType, ex.getMessage(), ex.getProperties());
    }

    // =========================================================================
    // BUSINESS >> VALIDATION
    // =========================================================================

    /**
     * Trata erros de validação de entrada.
     *
     * @param ex      exceção capturada
     * @param request requisição HTTP atual
     * @return resposta RFC 7807
     */
    private Response handleValidationException(ValidationException ex) {

        ErrorCategory category = ErrorCategory.VALIDATION;
        ErrorType errorType = TechnicalErrorType.INPUT_VALIDATION_ERROR;

        this.log(category, errorType, ex, ex.getMessage());

        return this.buildResponse(errorType, ex.getMessage(), ex.getErrors());
    }

    // =========================================================================
    // INTEGRATION
    // =========================================================================

    /**
     * Trata falhas em integrações externas.
     *
     * <p>
     * Enriquece automaticamente a resposta RFC 7807 com metadados operacionais da
     * integração.
     * </p>
     *
     * @param ex      exceção capturada
     * @param request requisição HTTP atual
     * @return resposta RFC 7807
     */
    public Response handleIntegrationException(IntegrationException ex) {

        ErrorCategory category = ErrorCategory.INTEGRATION;
        ErrorType errorType = ex.getErrorType();

        Map<String, Object> properties = new HashMap<>();

        properties.put("service", ex.getService());

        if (ex.getExternalError() != null) {
            properties.put("externalCode", ex.getExternalError().getCode());
            properties.put("externalStatus", ex.getExternalError().getStatus());
            properties.put("externalRetryable", ex.getExternalError().isRetryable());
        }

        if (ObjectUtils.isNotEmpty(ex.getMetadata())) {
            properties.putAll(ex.getMetadata());
        }

        this.log(category, errorType, ex, ex.getMessage());

        return this.buildResponse(errorType, ex.getMessage(), properties);
    }

    // =========================================================================
    // APPLICATION (fallback controlado de exceções da aplicação)
    // =========================================================================

    // APPLICATION -> NOT FOUND ===============================================

    /**
     * Trata recursos inexistentes.
     *
     * @param ex exceção capturada
     * @return resposta RFC 7807
     */
    private Response handleNotFoundException(NotFoundException ex) {

        ErrorCategory category = ErrorCategory.BUSINESS;
        ErrorType errorType = TechnicalErrorType.RESOURCE_NOT_FOUND;

        this.log(category, errorType, ex, ex.getMessage());

        return this.buildResponse(errorType, ex.getMessage());
    }

    // =========================================================================
    // APPLICATION (fallback controlado de exceções da aplicação)
    // =========================================================================

    /**
     * Trata exceções genéricas da aplicação.
     *
     * @param ex exceção capturada
     * @return resposta RFC 7807
     */
    private Response handleApplicationException(ApplicationException ex) {

        ErrorCategory category = ErrorCategory.APPLICATION;
        TechnicalErrorType errorType = TechnicalErrorType.INTERNAL_ERROR;

        this.log(category, errorType, ex, ex.getMessage());

        return this.buildResponse(errorType, ex.getMessage());
    }

    // =========================================================================
    // GENERIC / TECHNICAL
    // =========================================================================

    /**
     * Fallback técnico global.
     *
     * @param ex exceção capturada
     * @return resposta RFC 7807
     */
    private Response handleTechnicalException(Exception ex) {

        ErrorCategory category = ErrorCategory.TECHNICAL;
        TechnicalErrorType errorType = TechnicalErrorResolver.resolveType(ex);

        String detail = "Unexpected error";

        this.log(category, errorType, ex, detail);

        return this.buildResponse(errorType, detail);
    }

    // =========================================================================
    // BUILDERS
    // =========================================================================

    /**
     * Constrói uma resposta RFC 7807 simples.
     *
     * @param errorType erro interno
     * @param detail    detalhe do erro
     * @return resposta RFC 7807
     */
    private Response buildResponse(
            ErrorType errorType,
            String detail) {

        return this.buildResponse(errorType, detail, null, null);
    }

    /**
     * Constrói uma resposta RFC 7807 com propriedades adicionais.
     *
     * @param errorType  erro interno
     * @param detail     detalhe do erro
     * @param properties propriedades adicionais
     * @return resposta RFC 7807
     */
    private Response buildResponse(
            ErrorType errorType,
            String detail,
            Map<String, Object> properties) {

        return this.buildResponse(errorType, detail, null, properties);
    }

    /**
     * Constrói uma resposta RFC 7807 com erros de validação.
     *
     * @param errorType erro interno
     * @param detail    detalhe do erro
     * @param errors    erros de validação
     * @return resposta RFC 7807
     */
    private Response buildResponse(
            ErrorType errorType,
            String detail,
            List<ValidationError> errors) {

        return this.buildResponse(errorType, detail, errors, null);
    }

    /**
     * Constrói uma resposta RFC 7807 completa.
     *
     * @param request requisição HTTP
     * @param errorType erro interno
     * @param detail detalhe do erro
     * @param errors erros de validação
     * @param properties propriedades adicionais
     * @return response entity padronizada
     */
    private Response buildResponse(
            ErrorType errorType,
            String detail,
            List<ValidationError> errors,
            Map<String, Object> properties) {

        ErrorResponse.ErrorResponseBuilder responseBuilder = ErrorResponse.builder()
                .type(resolveTypeUri(errorType))
                .title(errorType.getTitle())
                .status(errorType.getHttpStatus())
                .detail(detail)
                .instance(this.resolveInstance())
                .timestamp(Instant.now())
                .correlationId(getCorrelationId());

        if (ObjectUtils.isNotEmpty(errors)) {
            responseBuilder.errors(errors);
        }

        Map<String, Object> finalProperties = this.buildProperties(errorType, properties);

        if (!finalProperties.isEmpty()) {
            responseBuilder.properties(finalProperties);
        }

        ErrorResponse body = responseBuilder.build();

        return Response.status(errorType.getHttpStatus())
                .type(MEDIA_TYPE_APPLICATION_PROBLEM_JSON)
                .entity(body)
                .build();
    }

    /**
     * Constrói propriedades adicionais da resposta RFC 7807.
     *
     * @param errorType erro interno
     * @param customProperties propriedades customizadas
     * @return mapa final de propriedades
     */
    private Map<String, Object> buildProperties(
            ErrorType errorType,
            Map<String, Object> customProperties) {

        Map<String, Object> properties = new HashMap<>();

        properties.put("errorCode", errorType.getCode());
        properties.put("retryable", errorType.isRetryable());

        if (ObjectUtils.isNotEmpty(customProperties)) {
            properties.putAll(customProperties);
        }

        return properties;
    }

    // =========================================================================
    // RESOLVERS
    // =========================================================================


    // =========================================================================
    // RESOLVERS
    // =========================================================================

    /**
     * Resolve a URI RFC 7807 do tipo do problema.
     *
     * @param errorType erro interno
     * @return URI do problema
     */
    private URI resolveTypeUri(ErrorType errorType) {
        return URI.create(this.uriInfo.getBaseUri() + problemBaseUri + "/" + errorType.getCode());
    }

    /**
     * Resolve a URI completa da requisição atual.
     *
     * @param request requisição HTTP
     * @return URI da requisição
     */
    private URI resolveInstance() {
        return this.uriInfo.getRequestUri();
    }

    /**
     * Obtém o correlationId atual.
     *
     * @return correlationId atual
     */
    private String getCorrelationId() {

        String correlationId = CorrelationContext.get();

        if (correlationId == null || correlationId.isBlank()) {
            return "no-correlation-id";
        }

        return correlationId;
    }

    // =========================================================================
    // LOGGING
    // =========================================================================

    /**
     * Realiza logging estruturado da falha.
     *
     * <p>
     * Atualmente o nível de log é resolvido
     * utilizando regras simples baseadas
     * na categoria do erro.
     * </p>
     *
     * <p>
     * O método já está preparado para futura
     * evolução utilizando resolvers
     * operacionais mais avançados
     * (severity, alerting, logLevel, etc).
     * </p>
     *
     * @param category  categoria do erro
     * @param errorType erro interno
     * @param ex        exceção original
     * @param detail    detalhe do erro
     */
    private void log(ErrorCategory category, ErrorType errorType, Exception ex, String detail) {

        String pattern = "[error][{}] code={} status={} retryable={} correlationId={} message={}";

        String correlationId = this.getCorrelationId();
        int status = errorType.getHttpStatus();

        // 5xx → erro técnico/infra
        if (status >= 500) {

            log.error(pattern,
                    category.getCode(),
                    errorType.getCode(),
                    status,
                    errorType.isRetryable(),
                    correlationId,
                    detail,
                    ex);
            return;
        }

        // 4xx -> opcional/configurável
        if (status >= 400 && this.logClientErrors) {
            // validação e negócio → warn sem stacktrace -> apenas para validação interna
            log.warn(
                    pattern,
                    category.getCode(),
                    errorType.getCode(),
                    status,
                    errorType.isRetryable(),
                    correlationId,
                    detail);
        }

        // default: não loga nada
    }
}