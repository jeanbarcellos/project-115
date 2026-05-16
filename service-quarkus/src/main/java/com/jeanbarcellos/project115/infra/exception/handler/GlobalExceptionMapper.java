package com.jeanbarcellos.project115.infra.exception.handler;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

import com.jeanbarcellos.core.error.ErrorResponse;
import com.jeanbarcellos.core.error.ErrorType;
import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.ValidationError;
import com.jeanbarcellos.core.exception.ApplicationException;
import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.ValidationException;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private String problemBaseUri = "problems";

    @Context
    UriInfo uriInfo;

    // Injeção do contexto da requisição atual
    @Context
    jakarta.ws.rs.container.ContainerRequestContext requestContext;

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof NotFoundException ex) {
            return this.handleNotFoundException(ex);
        }

        if (exception instanceof BusinessException ex) {
            return this.handleBusinessException(ex);
        }

        if (exception instanceof ValidationException ex) {
            return this.handleValidationException(ex);
        }

        if (exception instanceof ApplicationException ex) {
            return this.handleApplicationException(ex);
        }

        return this.handleException(exception);
    }

    // BUSINESS ===============================================================

    /**
     * Constrói a resposta para erros de regra de negócio (400).
     */
    private Response handleBusinessException(BusinessException ex) {

        ErrorType errorType = ex.getType();
        Map<String, Object> properties = ObjectUtils.isNotEmpty(ex.getProperties()) ? ex.getProperties(): null;

        this.log("business", errorType, ex, ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .type(this.resolveTypeUri(errorType))
                .title(errorType.getTitle())
                .status(errorType.getHttpStatus())
                .detail(ex.getMessage()) // Mensagem customizada da exception
                .instance(this.resolveInstance())
                .properties(properties) // Propriedades extras/contextos
                .build();

        return Response.status(errorType.getHttpStatus())
                .entity(errorResponse)
                .build();
    }

    // BUSINESS -> VALIDATION =================================================

    /**
     * Método especializado para tratar ValidationException (Status 400).
     */
    private Response handleValidationException(ValidationException ex) {

        TechnicalErrorType errorType = TechnicalErrorType.INPUT_VALIDATION_ERROR;
        List<ValidationError> errors = ex.getErrors();

        this.log("validation", errorType, ex, ex.getMessage());

        ErrorResponse errorResposne = ErrorResponse.builder()
                .type(resolveTypeUri(errorType))
                .title(errorType.getTitle())
                .status(errorType.getHttpStatus())
                .detail(ex.getMessage()) // Mensagem customizada da exception
                .instance(this.resolveInstance())
                .errors(errors) // Campo customizado de erros
                .build();

        return Response.status(errorType.getHttpStatus())
                .entity(errorResposne)
                .build();
    }

    // APPLICATION -> NOT FOUND ===============================================

    /**
     * Tratamento para erros de recurso não encontrado (404).
     */
    private Response handleNotFoundException(NotFoundException ex) {

        ErrorType errorType = TechnicalErrorType.RESOURCE_NOT_FOUND;

        // LOG
        this.log("business", errorType, ex, ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .type(this.resolveTypeUri(errorType))
                .title(errorType.getTitle())
                .status(errorType.getHttpStatus())
                .detail(ex.getMessage()) // Mensagem customizada da exception
                .instance(this.resolveInstance())
                .build();

        return Response.status(errorType.getHttpStatus())
                .entity(errorResponse)
                .build();
    }

    // APPLICATION (fallback controlado) ======================================

    /**
     * Constrói a resposta para erros de aplicação genéricos ou de negócio (500).
     */
    private Response handleApplicationException(ApplicationException ex) {

        // Sem tipo explícito → vira erro interno
        TechnicalErrorType errorType = TechnicalErrorType.INTERNAL_ERROR;

        // LOG
        this.log("technical", errorType, ex, ex.getMessage());

        ErrorResponse errorResposne = ErrorResponse.builder()
                .type(resolveTypeUri(errorType))
                .title(errorType.getTitle())
                .status(errorType.getHttpStatus())
                .detail(ex.getMessage()) // Mensagem customizada da exception
                .instance(this.resolveInstance())
                .build();

        return Response.status(errorType.getHttpStatus())
                .entity(errorResposne)
                .build();
    }

    // GENERIC / TECHNICAL ====================================================

    private Response handleException(Exception ex) {

        TechnicalErrorType errorType = TechnicalErrorResolver.resolveType(ex);
        String detail = "Unexpected error";

        // LOG
        this.log("technical", errorType, ex, ex.getMessage());

        ErrorResponse errorResposne = ErrorResponse.builder()
                .type(resolveTypeUri(errorType))
                .title(errorType.getTitle())
                .status(errorType.getHttpStatus())
                .detail(detail) // Não se expôem erro no endpoint, somente no log
                .instance(this.resolveInstance())
                .build();

        return Response.status(errorType.getHttpStatus())
                .entity(errorResposne)
                .build();
    }


    // RESOLVERS ==============================================================

    private URI resolveTypeUri(ErrorType errorType) {
        return URI.create(this.uriInfo.getBaseUri() + problemBaseUri + "/" + errorType.getCode());
    }

    private URI resolveInstance() {
        return URI.create(this.uriInfo.getPath());
    }

    // LOGGING ================================================================

    private void log(String category, ErrorType errorType, Exception ex, String detail) {

        // Erros não técnicos não é necessário logar
        String pattern = "[error][{}] code={} status={} retryable={} message={}";

        if (errorType.getHttpStatus() >= 500) {
            log.error(pattern,
                    category,
                    errorType.getCode(),
                    errorType.getHttpStatus(),
                    errorType.isRetryable(),
                    detail,
                    ex);
        } else {
            log.warn(pattern,
                    category,
                    errorType.getCode(),
                    errorType.getHttpStatus(),
                    errorType.isRetryable(),
                    detail);
        }
    }

}