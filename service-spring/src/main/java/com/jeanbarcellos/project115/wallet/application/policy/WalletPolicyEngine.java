package com.jeanbarcellos.project115.wallet.application.policy;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.jeanbarcellos.project115.wallet.domain.Wallet;

import lombok.RequiredArgsConstructor;

/**
 * Engine que executa todas as políticas registradas.
 */
@Component
@RequiredArgsConstructor
public class WalletPolicyEngine {

    private final List<WalletPolicy> policies;

    public void validate(Wallet wallet, BigDecimal amount) {

        for (WalletPolicy policy : this.policies) {
            policy.validate(wallet, amount);
        }
    }
}