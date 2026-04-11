package com.jeanbarcellos.project115.wallet.application.mapper;

import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.wallet.application.error.WalletErrorType;

public class WalletExceptionTranslator {

    private WalletExceptionTranslator() {
    }

    public static BusinessException translate(DomainException ex) {

        return switch (ex.getCode()) {

            case "INSUFFICIENT_BALANCE" ->
                new BusinessException(
                        WalletErrorType.INSUFFICIENT_BALANCE,
                        "Insufficient balance",
                        ex.getContext());

            case "INVALID_AMOUNT" ->
                new BusinessException(
                        WalletErrorType.INVALID_AMOUNT,
                        "Invalid amount",
                        ex.getContext());

            default ->
                new BusinessException(
                        WalletErrorType.INVALID_AMOUNT,
                        "Unknown domain error");
        };
    }
}