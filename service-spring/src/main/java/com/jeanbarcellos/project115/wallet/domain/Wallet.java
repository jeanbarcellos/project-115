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