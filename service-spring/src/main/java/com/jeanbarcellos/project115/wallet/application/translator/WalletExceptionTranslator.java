package com.jeanbarcellos.project115.wallet.application.translator;

import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.wallet.application.error.WalletErrorType;

/**
 * Tradutor de exceções de domínio para exceções de aplicação.
 */
public final class WalletExceptionTranslator {

    private WalletExceptionTranslator() {
    }

    public static BusinessException translate(DomainException exception) {

        String message = exception.getMessage();

        return switch (message) {

            case "INSUFFICIENT_BALANCE" ->
                new BusinessException(
                        WalletErrorType.INSUFFICIENT_BALANCE,
                        "Insufficient balance");

            case "INVALID_AMOUNT" ->
                new BusinessException(
                        WalletErrorType.INVALID_AMOUNT,
                        "Invalid amount");

            case "INVALID_TRANSFER" ->
                new BusinessException(
                        WalletErrorType.INVALID_TRANSFER,
                        "Invalid transfer");

            case "FRAUD_DETECTED" ->
                new BusinessException(
                        WalletErrorType.FRAUD_DETECTED,
                        "Fraud detected");

            case "IDEMPOTENCY_PAYLOAD_MISMATCH" ->
                new BusinessException(
                        WalletErrorType.IDEMPOTENT_PAYLOAD_MISMATCH,
                        "Payload mismatch for idempotency key");

            default ->
                new BusinessException(
                        WalletErrorType.INVALID_TRANSACTION,
                        "Unexpected domain error");
        };
    }
}