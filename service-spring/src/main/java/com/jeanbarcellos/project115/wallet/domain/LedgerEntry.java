package com.jeanbarcellos.project115.wallet.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entrada contábil (parte de uma Transaction).
 */
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    @Setter
    private Transaction transaction;

    public LedgerEntry(Long walletId, TransactionType type, BigDecimal amount) {

        if (walletId == null)
            throw new IllegalArgumentException("walletId is required");
        if (type == null)
            throw new IllegalArgumentException("type is required");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }

        this.id = UUID.randomUUID();
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.createdAt = Instant.now();
    }

    public BigDecimal getSignedAmount() {
        return this.type == TransactionType.DEBIT
                ? this.amount.negate()
                : this.amount;
    }
}