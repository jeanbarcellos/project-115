package com.jeanbarcellos.project115.wallet.domain;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Aggregate Wallet.
 *
 * Saldo NÃO é persistido — é derivado do ledger.
 */
@Entity
@Table(name = "wallet")
@Getter
@NoArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Lock otimista
     */
    @Version
    private Long version;

    public BigDecimal calculateBalance(List<LedgerEntry> entries) {
        return entries.stream()
                .map(LedgerEntry::getSignedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}