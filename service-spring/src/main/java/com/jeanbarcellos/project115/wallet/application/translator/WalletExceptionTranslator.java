package com.jeanbarcellos.project115.wallet.application.translator;

import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.wallet.application.error.WalletErrorType;
import com.jeanbarcellos.project115.wallet.domain.exception.InsufficientBalanceException;
import com.jeanbarcellos.project115.wallet.domain.exception.InvalidAmountException;
import com.jeanbarcellos.project115.wallet.domain.exception.InvalidTransactionException;

/**
 * Tradutor de exceções de domínio para exceções de aplicação.
 */
public final class WalletExceptionTranslator {

    private WalletExceptionTranslator() {
    }

    public static BusinessException translate(DomainException exception) {

        if (exception instanceof InsufficientBalanceException) {
            return new BusinessException(
                    WalletErrorType.INSUFFICIENT_BALANCE,
                    exception.getMessage());
        }

        if (exception instanceof InvalidAmountException) {
            return new BusinessException(
                    WalletErrorType.INVALID_AMOUNT,
                    exception.getMessage());
        }

        if (exception instanceof InvalidTransactionException) {
            return new BusinessException(
                    WalletErrorType.INVALID_TRANSACTION,
                    exception.getMessage());
        }

        String message = exception.getMessage();

        // ⚠️ Ainda string-based (ponto de melhoria futura)
        return switch (message) {

            case "INSUFFICIENT_BALANCE" ->
                new BusinessException(
                        WalletErrorType.INSUFFICIENT_BALANCE,
                        "Insufficient balance");

            case "INVALID_AMOUNT" ->
                new BusinessException(
                        WalletErrorType.INVALID_AMOUNT,
                        "Invalid amount");

            case "INVALID_TRANSACTION" ->
                new BusinessException(
                        WalletErrorType.INVALID_TRANSACTION,
                        "Invalid transaction");

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