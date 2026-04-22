package com.jeanbarcellos.project115.wallet.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.jeanbarcellos.core.error.DomainViolation;
import com.jeanbarcellos.core.exception.DomainValidationException;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Aggregate raiz Wallet.
 *
 * - saldo NÃO é persistido diretamente
 * - balanceSnapshot otimiza leitura
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
     * Controle de concorrência (ETag)
     */
    @Version
    private Long version;

    /**
     * Snapshot de saldo (performance)
     */
    private BigDecimal balanceSnapshot = BigDecimal.ZERO;

    public Wallet(BigDecimal initialBalance) {

        List<DomainViolation> violations = new ArrayList<>();

        if (initialBalance == null) {
            violations.add(new DomainViolation("initialBalance", "must not be null", null));
        }

        if (initialBalance != null && initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            violations.add(new DomainViolation("initialBalance", "must be >= 0", initialBalance));
        }

        if (!violations.isEmpty()) {
            throw new DomainValidationException("Invalid wallet creation", violations);
        }
    }

    public BigDecimal calculateBalance(List<LedgerEntry> entries) {

        BigDecimal delta = entries.stream()
                .map(LedgerEntry::getSignedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return this.balanceSnapshot.add(delta);
    }

    public void updateSnapshot(BigDecimal newBalance) {
        this.balanceSnapshot = newBalance;
    }
}