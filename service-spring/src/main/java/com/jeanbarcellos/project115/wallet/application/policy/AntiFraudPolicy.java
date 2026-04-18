package com.jeanbarcellos.project115.wallet.application.policy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.wallet.application.repository.LedgerEntryRepository;
import com.jeanbarcellos.project115.wallet.domain.LedgerEntry;
import com.jeanbarcellos.project115.wallet.domain.Wallet;

import lombok.RequiredArgsConstructor;

/**
 * Política antifraude.
 */
@Component
@RequiredArgsConstructor
public class AntiFraudPolicy implements WalletPolicy {

    private final LedgerEntryRepository ledgerEntryRepository;

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("10000");

    @Override
    public void validate(Wallet wallet, BigDecimal amount) {

        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new DomainException("FRAUD_DETECTED");
        }

        List<LedgerEntry> recent =
                this.ledgerEntryRepository.findByWalletId(wallet.getId());

        long count = recent.stream()
                .filter(entry ->
                        entry.getCreatedAt().isAfter(Instant.now().minusSeconds(60)))
                .count();

        if (count > 5) {
            throw new DomainException("FRAUD_DETECTED");
        }
    }
}