package com.jeanbarcellos.project115.wallet.domain.exception;

import com.jeanbarcellos.core.exception.DomainException;

/**
 * Valor inválido.
 */
public class InvalidAmountException extends DomainException {

    public InvalidAmountException() {
        super("Invalid amount");
    }
}