package com.jeanbarcellos.project115.infra.exception.handler;

import java.util.Map;

import static com.jeanbarcellos.core.Constants.CORRELATION_ID_HEADER;
import static com.jeanbarcellos.core.Constants.CORRELATION_ID_KEY;

import com.jeanbarcellos.core.error.ErrorResponse;
import com.jeanbarcellos.core.error.ErrorType;
import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.core.observability.CorrelationContext;
import com.jeanbarcellos.project115.infra.adapter.QuarkusProblemMapper;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<DomainException> {

    @Context
    UriInfo uriInfo;

    // Injeção do contexto da requisição atual
    @Context
    jakarta.ws.rs.container.ContainerRequestContext requestContext;

    @Override
    public Response toResponse(DomainException ex) {

        ErrorType type = TechnicalErrorType.INTERNAL_ERROR;

        // 1. Tenta pegar do ThreadLocal (CorrelationContext)
        String correlationId = CorrelationContext.get();

        // 2. Fallback: Se o ThreadLocal já foi limpo, recupera do contexto da requisição
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = (String) requestContext.getProperty(CORRELATION_ID_KEY);
        }

        var url = requestContext.getUriInfo().getRequestUri();

        // String correlationContext2 = CorrelationContext.get();
        // String correlationContext = "";
        // log.info("correlationContext: {}", correlationContext);
        // log.info("correlationContext2: {}", correlationContext2);

        ErrorResponse apiError = null;
        // ErrorResponse apiError = new ErrorResponse(
        //         url,
        //         type.getTitle(),
        //         type.getHttpStatus(),
        //         ex.getMessage(),
        //         uriInfo.getRequestUri(),
        //         Map.of(CORRELATION_ID_KEY, correlationId));

        return QuarkusProblemMapper.toResponse(apiError);
    }
}