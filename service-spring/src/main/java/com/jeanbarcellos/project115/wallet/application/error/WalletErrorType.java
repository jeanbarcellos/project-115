package com.jeanbarcellos.project115.wallet.application.error;

import com.jeanbarcellos.core.error.ErrorType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WalletErrorType implements ErrorType {

    WALLET_NOT_FOUND("wallet-not-found", 404, "Wallet not found", false),

    INSUFFICIENT_BALANCE("insufficient-balance", 409, "Insufficient balance", false),

    INVALID_AMOUNT("invalid-amount", 422, "Invalid amount", false),

    INVALID_TRANSACTION("invalid-transaction", 422, "Invalid transaction", false),

    INVALID_TRANSFER("invalid-transfer", 422, "Invalid transfer", false),

    IDEMPOTENT_CONFLICT("idempotent-conflict", 409, "Idempotent conflict", false),

    IDEMPOTENT_PAYLOAD_MISMATCH("idempotent-payload-mismatch", 409, "Payload mismatch", false),

    FRAUD_DETECTED("fraud-detected", 403, "Fraud detected", false);

    private final String code;
    private final int httpStatus;
    private final String title;
    private final boolean isRetryable;

}