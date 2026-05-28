package com.jeanbarcellos.project115.infra.controller;

import java.net.URI;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jeanbarcellos.core.exception.integration.ExternalSystemException;
import com.jeanbarcellos.core.exception.integration.HttpIntegrationException;
import com.jeanbarcellos.core.exception.integration.MessagingIntegrationException;
import com.jeanbarcellos.core.exception.integration.RpcIntegrationException;
import com.jeanbarcellos.core.exception.integration.StorageIntegrationException;
import com.jeanbarcellos.project115.endereco.SerproErrorType;

@RestController
@RequestMapping("/test/exceptions/integrations")
public class IntegrationExceptionTestController {

    /**
     * Simula uma falha HTTP REST.
     */
    @GetMapping("/http")
    public void http() {

        Map<String, String> responsePayload = Map.of(
                "code", "NOT_FOUND",
                "message", "Address not found");
        Map<String, Object> metadata = Map.of(
                "requestId", "REQ-123456",
                "endpoint", "/api/address",
                "traceId", "TRACE-ABC-001");

        throw new HttpIntegrationException(
                "rest-serpro",
                "GET",
                URI.create("https://serpro.gov.br/api/address"),
                404,
                responsePayload,
                "Error calling Serpro API",
                metadata,
                SerproErrorType.NOT_FOUND,
                new RuntimeException("HTTP integration failure"));
    }

    /**
     * Simula timeout HTTP local (sem erro oficial do provider).
     */
    @GetMapping("/http-timeout")
    public void httpTimeout() {

        throw new HttpIntegrationException(
                "rest-serpro",
                "GET",
                URI.create("https://serpro.gov.br/api/address"),
                504,
                null,
                "Timeout calling Serpro API",
                Map.of(
                        "timeout", "5000ms",
                        "traceId", "TRACE-TIMEOUT-001"),
                null,
                new RuntimeException("SocketTimeoutException"));
    }

    /**
     * Simula falha RPC.
     */
    @GetMapping("/rpc")
    public void rpc() {

        throw new RpcIntegrationException(
                "grpc-ledger",
                "LedgerService/CreateTransaction",
                "Error calling Ledger RPC",
                Map.of(
                        "traceId", "TRACE-RPC-001",
                        "grpcStatus", "UNAVAILABLE"),
                null,
                new RuntimeException("gRPC unavailable"));
    }

    /**
     * Simula falha em mensageria.
     */
    @GetMapping("/messaging")
    public void messaging() {

        throw new MessagingIntegrationException(
                "kafka-wallet-events",
                "wallet.transaction.created",
                "publish",
                "Error publishing Kafka event",
                Map.of(
                        "partition", 3,
                        "offset", 1550,
                        "traceId", "TRACE-KAFKA-001"),
                null,
                new RuntimeException("Kafka unavailable"));
    }

    /**
     * Simula falha em storage externo.
     */
    @GetMapping("/storage")
    public void storage() {

        throw new StorageIntegrationException(
                "s3-documents",
                "bucket/customer-documents/file.pdf",
                "Error uploading file to S3",
                Map.of(
                        "bucket", "customer-documents",
                        "region", "us-east-1",
                        "traceId", "TRACE-S3-001"),
                null,
                new RuntimeException("S3 unavailable"));
    }

    /**
     * Simula falha genérica em sistema externo.
     */
    @GetMapping("/external-system")
    public void externalSystem() {

        throw new ExternalSystemException(
                "legacy-mainframe",
                "External system unavailable",
                Map.of(
                        "host", "legacy-host-01",
                        "traceId", "TRACE-LEGACY-001"),
                null,
                new RuntimeException("Legacy system unavailable"));
    }

    /**
     * Simula erro documentado do provider.
     */
    @GetMapping("/provider-error")
    public void providerError() {

        throw new HttpIntegrationException(
                "rest-serpro",
                "POST",
                URI.create("https://serpro.gov.br/api/customer"),
                400,
                Map.of(
                        "code", "INVALID_REQUEST",
                        "message", "Invalid CPF"),
                "Provider returned validation error",
                Map.of(
                        "traceId", "TRACE-PROVIDER-001",
                        "providerRequestId", "SP-998877"),
                SerproErrorType.INVALID_REQUEST,
                new RuntimeException("Provider validation failure"));
    }

    /**
     * Simula erro interno do provider.
     */
    @GetMapping("/provider-internal-error")
    public void providerInternalError() {

        throw new HttpIntegrationException(
                "rest-serpro",
                "POST",
                URI.create("https://serpro.gov.br/api/customer"),
                500,
                Map.of(
                        "code", "INTERNAL_ERROR",
                        "message", "Unexpected provider failure"),
                "Provider internal error",
                Map.of(
                        "traceId", "TRACE-PROVIDER-500",
                        "providerRequestId", "SP-500-001"),
                SerproErrorType.INTERNAL_ERROR,
                new RuntimeException("Provider internal failure"));
    }
}