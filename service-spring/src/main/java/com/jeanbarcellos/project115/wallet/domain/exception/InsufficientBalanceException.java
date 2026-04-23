package com.jeanbarcellos.project115.wallet.domain.exception;

import com.jeanbarcellos.core.exception.DomainException;

/**
 * Saldo insuficiente.
 */
public class InsufficientBalanceException extends DomainException {

    public InsufficientBalanceException() {
        super("Insufficient balance");
    }
}