package com.jeanbarcellos.project115.wallet.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.jeanbarcellos.core.exception.DomainException;

import lombok.Getter;

/**
 * Transação contábil (double-entry).
 */
@Getter
public class Transaction {

    private UUID id;
    private List<LedgerEntry> entries;
    private Instant timestamp;

    public Transaction(List<LedgerEntry> entries) {

        if (entries == null || entries.size() < 2) {
            throw new DomainException("INVALID_TRANSACTION");
        }

        validateBalanced(entries);

        this.id = UUID.randomUUID();
        this.entries = entries;
        this.timestamp = Instant.now();
    }

    private void validateBalanced(List<LedgerEntry> entries) {

        BigDecimal total = entries.stream()
                .map(LedgerEntry::getSignedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(BigDecimal.ZERO) != 0) {
            throw new DomainException("UNBALANCED_TRANSACTION");
        }
    }

}