package com.jeanbarcellos.project115.wallet.application.command;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class TransferCommand implements WalletCommand {

    private final Long sourceWalletId;
    private final Long targetWalletId;
    private final BigDecimal amount;
    private final Long expectedVersion;
    private final String idempotencyKey;

    @Override
    public Long getWalletId() {
        return this.sourceWalletId;
    }

    @Override
    public String getOperation() {
        return "TRANSFER";
    }
}