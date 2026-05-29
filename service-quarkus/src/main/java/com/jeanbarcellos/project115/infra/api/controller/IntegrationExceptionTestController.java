package com.jeanbarcellos.project115.infra.api.controller;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.jeanbarcellos.core.exception.integration.ExternalSystemException;
import com.jeanbarcellos.core.exception.integration.HttpIntegrationException;
import com.jeanbarcellos.core.exception.integration.MessagingIntegrationException;
import com.jeanbarcellos.core.exception.integration.RpcIntegrationException;
import com.jeanbarcellos.core.exception.integration.StorageIntegrationException;
import com.jeanbarcellos.project115.user.error.SerproErrorType;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Controller auxiliar para validação dos handlers
 * de exceções de integração.
 *
 * <p>
 * Permite simular falhas de integrações externas
 * para validar:
 * </p>
 *
 * <ul>
 *   <li>RFC 7807;</li>
 *   <li>GlobalExceptionMapper;</li>
 *   <li>logging estruturado;</li>
 *   <li>correlationId;</li>
 *   <li>metadados operacionais;</li>
 *   <li>mapeamento de erros externos.</li>
 * </ul>
 *
 * <p>
 * Deve existir apenas em ambientes de desenvolvimento.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@Tag(name = "test-exceptions")
@Path("/test/exceptions/integrations")
@Produces(MediaType.APPLICATION_JSON)
public class IntegrationExceptionTestController {

    // =========================================================================
    // HTTP
    // =========================================================================

    /**
     * Simula retorno 404 de provider externo.
     */
    @GET
    @Path("/http/not-found")
    public void httpNotFound() {

        throw new HttpIntegrationException(
                "rest-serpro",
                "GET",
                URI.create("https://api.serpro.gov.br/v1/customers/777"),
                404,
                "Customer not found",
                """
                {
                  "code": "NOT_FOUND",
                  "message": "Customer not found"
                }
                """,
                Map.of(
                        "providerRequestId", UUID.randomUUID().toString(),
                        "endpoint", "/v1/customers/777"),
                SerproErrorType.NOT_FOUND,
                null);
    }

    /**
     * Simula erro de validação retornado pelo provider.
     */
    @GET
    @Path("/http/bad-request")
    public void httpBadRequest() {

        throw new HttpIntegrationException(
                "rest-serpro",
                "POST",
                URI.create("https://api.serpro.gov.br/v1/customers"),
                400,
                "Invalid request",
                """
                {
                  "code": "INVALID_REQUEST",
                  "message": "Document is invalid"
                }
                """,
                Map.of(
                        "document", "123"),
                SerproErrorType.INVALID_REQUEST,
                null);
    }

    /**
     * Simula indisponibilidade do provider.
     */
    @GET
    @Path("/http/internal-error")
    public void httpInternalError() {

        throw new HttpIntegrationException(
                "rest-serpro",
                "GET",
                URI.create("https://api.serpro.gov.br/v1/customers"),
                500,
                "Internal provider error",
                """
                {
                  "code": "INTERNAL_ERROR",
                  "message": "Unexpected provider failure"
                }
                """,
                Map.of(
                        "providerRequestId", UUID.randomUUID().toString()),
                SerproErrorType.INTERNAL_ERROR,
                new ConnectException("Connection refused"));
    }

    /**
     * Simula timeout de integração.
     */
    @GET
    @Path("/http/timeout")
    public void httpTimeout() {

        throw new HttpIntegrationException(
                "rest-serpro",
                "GET",
                URI.create("https://api.serpro.gov.br/v1/customers"),
                504,
                "Timeout calling Serpro API",
                null,
                Map.of(
                        "timeout", "5000ms",
                        "attempt", 3),
                SerproErrorType.TIMEOUT,
                new SocketTimeoutException("Read timed out"));
    }

    // =========================================================================
    // RPC
    // =========================================================================

    /**
     * Simula falha em integração gRPC.
     */
    @GET
    @Path("/rpc")
    public void rpc() {

        throw new RpcIntegrationException(
                "grpc-ledger",
                "LedgerService/CreateTransaction",
                "Ledger unavailable",
                Map.of(
                        "grpcStatus", "UNAVAILABLE",
                        "attempt", 2),
                null,
                new RuntimeException("gRPC unavailable"));
    }

    // =========================================================================
    // MESSAGING
    // =========================================================================

    /**
     * Simula falha ao publicar evento.
     */
    @GET
    @Path("/messaging/publish")
    public void messagingPublish() {

        throw new MessagingIntegrationException(
                "kafka-wallet-events",
                "wallet.created",
                "publish",
                "Error publishing event",
                Map.of(
                        "partition", 5,
                        "topic", "wallet.created"),
                null,
                new RuntimeException("Broker unavailable"));
    }

    /**
     * Simula falha ao consumir evento.
     */
    @GET
    @Path("/messaging/consume")
    public void messagingConsume() {

        throw new MessagingIntegrationException(
                "kafka-wallet-events",
                "wallet.created",
                "consume",
                "Error consuming event",
                Map.of(
                        "partition", 2,
                        "offset", 99999L),
                null,
                new RuntimeException("Consumer failure"));
    }

    // =========================================================================
    // STORAGE
    // =========================================================================

    /**
     * Simula falha em upload para storage.
     */
    @GET
    @Path("/storage/upload")
    public void storageUpload() {

        throw new StorageIntegrationException(
                "s3-documents",
                "documents/customer/document.pdf",
                "Error uploading file",
                Map.of(
                        "bucket", "customer-documents",
                        "region", "us-east-1"),
                null,
                new RuntimeException("S3 unavailable"));
    }

    /**
     * Simula falha em download de storage.
     */
    @GET
    @Path("/storage/download")
    public void storageDownload() {

        throw new StorageIntegrationException(
                "redis-cache",
                "customer:123",
                "Error reading cache entry",
                Map.of(
                        "ttl", 300),
                null,
                new RuntimeException("Redis unavailable"));
    }

    // =========================================================================
    // GENERIC
    // =========================================================================

    /**
     * Simula falha genérica de sistema externo.
     */
    @GET
    @Path("/external-system")
    public void externalSystem() {

        throw new ExternalSystemException(
                "legacy-mainframe",
                "Legacy system unavailable",
                Map.of(
                        "host", "legacy-mainframe-01",
                        "environment", "prd"),
                null,
                new RuntimeException("Host unreachable"));
    }
}