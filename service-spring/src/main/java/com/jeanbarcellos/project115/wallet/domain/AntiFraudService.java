package com.jeanbarcellos.project115.wallet.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.jeanbarcellos.core.exception.DomainException;

public class AntiFraudService {

    private static final BigDecimal MAX = new BigDecimal("10000");

    public void validate(BigDecimal amount, List<LedgerEntry> recent) {

        if (amount.compareTo(MAX) > 0) {
            throw new DomainException("FRAUD_HIGH_VALUE");
        }

        long lastMinute = recent.stream()
                .filter(e -> e.getCreatedAt()
                        .isAfter(Instant.now().minusSeconds(60)))
                .count();

        if (lastMinute > 5) {
            throw new DomainException("FRAUD_RATE_LIMIT");
        }
    }
}