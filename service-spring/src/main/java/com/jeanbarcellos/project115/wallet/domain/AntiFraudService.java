package com.jeanbarcellos.project115.wallet.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.jeanbarcellos.core.exception.DomainException;

public class AntiFraudService {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("10000");

    public void validate(BigDecimal amount, List<LedgerEntry> recentEntries) {

        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new DomainException("FRAUD_DETECTED");
        }

        long recentCount = recentEntries.stream()
                .filter(entry -> entry.getCreatedAt().isAfter(Instant.now().minusSeconds(60)))
                .count();

        if (recentCount > 5) {
            throw new DomainException("FRAUD_DETECTED");
        }
    }
}