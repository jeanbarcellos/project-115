package com.jeanbarcellos.project115.wallet.application.error;

import com.jeanbarcellos.core.error.ErrorType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(fluent = true) // retira o prefixo getsset
public enum WalletErrorType implements ErrorType {

    WALLET_NOT_FOUND("wallet-not-found", 404, "Wallet not found"),
    INSUFFICIENT_BALANCE("insufficient-balance", 409, "Insufficient balance"),
    INVALID_AMOUNT("invalid-amount", 422, "Invalid amount");

    private final String code;
    private final int httpStatus;
    private final String title;

}