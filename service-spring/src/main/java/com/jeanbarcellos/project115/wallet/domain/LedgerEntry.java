package com.jeanbarcellos.project115.wallet.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ledger_entry")
@Getter
@NoArgsConstructor
public class LedgerEntry {

    @Id
    private UUID id;

    private Long walletId;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private BigDecimal amount;

    private Instant createdAt;

    public LedgerEntry(Long walletId, TransactionType type, BigDecimal amount) {

        if (walletId == null)
            throw new IllegalArgumentException();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException();

        this.id = UUID.randomUUID();
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.createdAt = Instant.now();
    }

    public BigDecimal getSignedAmount() {
        return type == TransactionType.DEBIT
                ? amount.negate()
                : amount;
    }
}