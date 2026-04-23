package com.jeanbarcellos.project115.wallet.domain.exception;

import com.jeanbarcellos.core.exception.DomainException;

/**
 * Transação inválida.
 */
public class InvalidTransactionException extends DomainException {

    public InvalidTransactionException() {
        super("Invalid transaction");
    }
}