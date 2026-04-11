package com.jeanbarcellos.project115.wallet.application.translator;

import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.wallet.application.error.WalletErrorType;

public class WalletExceptionTranslator {

    public static BusinessException translate(DomainException ex) {

        return switch (ex.getMessage()) {

            case "INSUFFICIENT_BALANCE" ->
                new BusinessException(
                        WalletErrorType.INSUFFICIENT_BALANCE,
                        "Insufficient balance");

            case "UNBALANCED_TRANSACTION" ->
                new BusinessException(
                        WalletErrorType.INVALID_TRANSACTION,
                        "Transaction is not balanced");

            default ->
                new BusinessException(
                        WalletErrorType.INVALID_TRANSACTION,
                        "Invalid transaction");
        };
    }
}