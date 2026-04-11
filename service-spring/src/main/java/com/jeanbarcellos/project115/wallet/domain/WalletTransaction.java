package com.jeanbarcellos.project115.wallet.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.Getter;

@Getter
public class WalletTransaction {

    private UUID id;
    private Long walletId;
    private TransactionType type;
    private BigDecimal amount;
    private Instant timestamp;

    public WalletTransaction(Long walletId, TransactionType type, BigDecimal amount) {
        this.id = UUID.randomUUID();
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.timestamp = Instant.now();
    }
}