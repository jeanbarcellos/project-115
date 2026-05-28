package com.jeanbarcellos.project115.infra.api.controller;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jeanbarcellos.core.error.DomainViolation;
import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.error.ValidationError;
import com.jeanbarcellos.core.exception.ApplicationException;
import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.core.exception.DomainValidationException;
import com.jeanbarcellos.core.exception.ValidationException;
import com.jeanbarcellos.core.exception.integration.ExternalSystemException;
import com.jeanbarcellos.core.exception.integration.HttpIntegrationException;
import com.jeanbarcellos.core.exception.integration.MessagingIntegrationException;
import com.jeanbarcellos.core.exception.integration.RpcIntegrationException;
import com.jeanbarcellos.core.exception.integration.StorageIntegrationException;
import com.jeanbarcellos.project115.endereco.SerproErrorType;
import com.jeanbarcellos.project115.user.application.error.UserErrorType;
import com.jeanbarcellos.project115.wallet.application.error.WalletErrorType;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controller auxiliar para testes e validação
 * dos handlers globais de exceção da aplicação.
 *
 * <p>
 * Objetivos:
 * </p>
 *
 * <ul>
 * <li>validar RFC 7807;</li>
 * <li>testar GlobalExceptionHandler;</li>
 * <li>testar observabilidade;</li>
 * <li>validar logs estruturados;</li>
 * <li>simular cenários reais de falha.</li>
 * </ul>
 *
 * <p>
 * IMPORTANTE:
 * </p>
 *
 * <p>
 * Este controller deve existir apenas em ambientes
 * de desenvolvimento/teste.
 * </p>
 *
 * @author Jean Barcellos <jeanbarcellos@hotmail.com>
 */
@RestController
@Tag(name = "test-exceptions")
@RequestMapping(value = "/test/exceptions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ExceptionTestController {

    // =========================================================================
    // DOMAIN
    // =========================================================================

    /**
     * Simula exceção simples de domínio.
     */
    @GetMapping("/domain")
    public void domainException() {

        throw new DomainException(
                "Wallet with id 777 does not exist");
    }

    /**
     * Simula exceção de domínio com contexto.
     */
    @GetMapping("/domain/context")
    public void domainExceptionWithContext() {

        throw new DomainException(
                "Wallet cannot be activated",
                Map.of(
                        "walletId", 777,
                        "status", "BLOCKED",
                        "operation", "activate"));
    }

    /**
     * Simula validação de domínio.
     */
    @GetMapping("/domain/validation")
    public void domainValidationException() {

        List<DomainViolation> violations = List.of(
                new DomainViolation(
                        "initialBalance",
                        "must be greater than zero",
                        -10),

                new DomainViolation(
                        "document",
                        "invalid cpf",
                        "123456"));

        throw new DomainValidationException(
                "Invalid wallet creation",
                violations);
    }

    // =========================================================================
    // BUSINESS
    // =========================================================================

    /**
     * Simula erro de negócio simples.
     */
    @GetMapping("/business")
    public void businessException() {

        throw new BusinessException(
                UserErrorType.USER_NOT_FOUND,
                "User with id 777 was not found");
    }

    /**
     * Simula erro de negócio com propriedades.
     */
    @GetMapping("/business/properties")
    public void businessExceptionWithProperties() {

        throw new BusinessException(
                UserErrorType.USER_NOT_FOUND,
                "User with id 777 was not found",
                Map.of(
                        "userId", 777,
                        "document", "12345678900",
                        "origin", "wallet-service"));
    }

    /**
     * Simula conflito de negócio.
     */
    @GetMapping("/business/conflict")
    public void businessConflictException() {

        throw new BusinessException(
                WalletErrorType.IDEMPOTENT_CONFLICT,
                "User already exists",
                Map.of(
                        "email", "john.doe@email.com"));
    }

    // =========================================================================
    // VALIDATION
    // =========================================================================

    /**
     * Simula erro de validação simples.
     */
    @GetMapping("/validation")
    public void validationException() {

        List<ValidationError> errors = List.of(
                ValidationError.of(
                        "user.cpf",
                        "Invalid CPF",
                        "123456"),

                ValidationError.of(
                        "email",
                        "Invalid email",
                        "invalid-email"),

                ValidationError.of(
                        "birthDate",
                        "Birth date cannot be future"));

        throw new ValidationException(
                "Validation failed",
                errors);
    }

    /**
     * Simula payload muito grande.
     */
    @GetMapping("/validation/payload")
    public void validationPayloadException() {

        List<ValidationError> errors = List.of(
                ValidationError.of(
                        "file",
                        "File size exceeds maximum allowed limit",
                        "50MB"));

        throw new ValidationException(
                "Payload validation failed",
                errors);
    }

    // =========================================================================
    // INTEGRATION - HTTP
    // =========================================================================

    /**
     * Simula 404 retornado por provider externo.
     */
    @GetMapping("/integration/http/not-found")
    public void integrationHttpNotFound() {

        throw new HttpIntegrationException(
                "rest-serpro",
                "GET",
                URI.create("https://serpro.gov.br/api/customers/777"),
                404,
                Map.of(
                        "code", "NOT_FOUND",
                        "message", "Customer not found"),
                "Customer not found in provider",
                Map.of(
                        "providerRequestId", UUID.randomUUID().toString(),
                        "endpoint", "/customers/777"),
                SerproErrorType.NOT_FOUND,
                null);
    }

    /**
     * Simula timeout HTTP.
     */
    @GetMapping("/integration/http/timeout")
    public void integrationHttpTimeout() {

        throw new HttpIntegrationException(
                "rest-serpro",
                "GET",
                URI.create("https://serpro.gov.br/api/customers"),
                504,
                null,
                "Timeout calling external provider",
                Map.of(
                        "timeout", "5000ms",
                        "attempt", 3),
                SerproErrorType.TIMEOUT,
                new SocketTimeoutException("Read timed out"));
    }

    /**
     * Simula indisponibilidade HTTP.
     */
    @GetMapping("/integration/http/unavailable")
    public void integrationHttpUnavailable() {

        throw new HttpIntegrationException(
                "rest-ibge",
                "GET",
                URI.create("https://ibge.gov.br/api/states"),
                503,
                Map.of(
                        "message", "Service unavailable"),
                "External service unavailable",
                Map.of(
                        "host", "ibge.gov.br",
                        "retry", true),
                SerproErrorType.INTERNAL_ERROR,
                new ConnectException("Connection refused"));
    }

    /**
     * Simula erro 400 externo.
     */
    @GetMapping("/integration/http/bad-request")
    public void integrationHttpBadRequest() {

        throw new HttpIntegrationException(
                "rest-serpro",
                "POST",
                URI.create("https://serpro.gov.br/api/customers"),
                400,
                Map.of(
                        "message", "Invalid document"),
                "Provider validation error",
                Map.of(
                        "document", "123"),
                SerproErrorType.INVALID_REQUEST,
                null);
    }

    // =========================================================================
    // INTEGRATION - RPC
    // =========================================================================

    /**
     * Simula falha RPC.
     */
    @GetMapping("/integration/rpc")
    public void rpcIntegrationException() {

        throw new RpcIntegrationException(
                "grpc-ledger",
                "LedgerService/CreateTransaction",
                "gRPC service unavailable",
                Map.of(
                        "grpcStatus", "UNAVAILABLE",
                        "attempt", 2),
                null,
                new RuntimeException("gRPC unavailable"));
    }

    // =========================================================================
    // INTEGRATION - MESSAGING
    // =========================================================================

    /**
     * Simula falha em publicação Kafka.
     */
    @GetMapping("/integration/messaging")
    public void messagingIntegrationException() {

        throw new MessagingIntegrationException(
                "kafka-wallet-events",
                "wallet.created",
                "publish",
                "Error publishing Kafka event",
                Map.of(
                        "partition", 5,
                        "offset", 999),
                null,
                new RuntimeException("Kafka broker unavailable"));
    }

    // =========================================================================
    // INTEGRATION - STORAGE
    // =========================================================================

    /**
     * Simula falha em storage.
     */
    @GetMapping("/integration/storage")
    public void storageIntegrationException() {

        throw new StorageIntegrationException(
                "s3-documents",
                "documents/customer/file.pdf",
                "Error uploading file",
                Map.of(
                        "bucket", "customer-documents",
                        "region", "us-east-1"),
                null,
                new RuntimeException("S3 unavailable"));
    }

    // =========================================================================
    // INTEGRATION - GENERIC
    // =========================================================================

    /**
     * Simula falha genérica em sistema externo.
     */
    @GetMapping("/integration/external-system")
    public void externalSystemException() {

        throw new ExternalSystemException(
                "legacy-mainframe",
                "Legacy system unavailable",
                Map.of(
                        "host", "legacy-server-01"),
                null,
                new RuntimeException("Mainframe unavailable"));
    }

    // =========================================================================
    // APPLICATION
    // =========================================================================

    /**
     * Simula exceção genérica da aplicação.
     */
    @GetMapping("/application")
    public void applicationException() {

        throw new ApplicationException(
                "Application processing error");
    }

    // =========================================================================
    // TECHNICAL
    // =========================================================================

    /**
     * Simula exceção SQL.
     */
    @GetMapping("/technical/sql")
    public void sqlException() throws SQLException {

        throw new SQLException(
                "Database connection lost");
    }

    /**
     * Simula violação de constraint.
     */
    @GetMapping("/technical/data-integrity")
    public void dataIntegrityException() {

        throw new DataIntegrityViolationException(
                "Unique constraint violation");
    }

    /**
     * Simula null pointer.
     */
    @GetMapping("/technical/null-pointer")
    public void nullPointerException() {

        throw new NullPointerException(
                "Simulated NPE");
    }

    /**
     * Simula illegal state.
     */
    @GetMapping("/technical/illegal-state")
    public void illegalStateException() {

        throw new IllegalStateException(
                "Invalid application state");
    }

    /**
     * Simula runtime exception genérica.
     */
    @GetMapping("/technical/runtime")
    public void runtimeException() {

        throw new RuntimeException(
                "Unexpected runtime failure");
    }

    /**
     * Simula erro técnico customizado.
     */
    @GetMapping("/technical/custom")
    public void technicalCustomException() {

        throw new RuntimeException(
                TechnicalErrorType.CONNECTION_ERROR.getTitle());
    }
}