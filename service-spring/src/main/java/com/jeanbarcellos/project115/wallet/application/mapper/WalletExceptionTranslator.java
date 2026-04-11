package com.jeanbarcellos.project115.wallet.application.mapper;

import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.wallet.application.error.WalletErrorType;

public class WalletExceptionTranslator {

    private WalletExceptionTranslator() {
    }

    public static BusinessException translate(DomainException ex) {

        String message = ex.getMessage();

        if (message.contains("Insufficient")) { // Alerta
            return new BusinessException(
                    WalletErrorType.INSUFFICIENT_BALANCE,
                    message,
                    ex.getContext());
        }

        if (message.contains("Amount")) { // Alerta
            return new BusinessException(
                    WalletErrorType.INVALID_AMOUNT,
                    message,
                    ex.getContext());
        }

        return new BusinessException(
                WalletErrorType.INVALID_AMOUNT,
                message);
    }
}