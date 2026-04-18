package com.jeanbarcellos.project115.wallet.application.policy;

import java.math.BigDecimal;

import com.jeanbarcellos.project115.wallet.domain.Wallet;

/**
 * Contrato de políticas de validação.
 */
public interface WalletPolicy {

    void validate(Wallet wallet, BigDecimal amount);
}