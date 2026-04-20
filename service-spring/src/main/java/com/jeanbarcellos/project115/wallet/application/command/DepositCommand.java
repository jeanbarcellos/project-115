package com.jeanbarcellos.project115.wallet.application.command;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class DepositCommand implements WalletCommand {

    private final Long walletId;
    private final BigDecimal amount;
    private final Long expectedVersion;
    private final String idempotencyKey;

    @Override
    public String getOperation() {
        return "DEPOSIT";
    }
}