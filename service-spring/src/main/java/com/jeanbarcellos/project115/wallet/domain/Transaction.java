package com.jeanbarcellos.project115.wallet.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.jeanbarcellos.core.exception.DomainException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wallet_transaction", uniqueConstraints = {
        @UniqueConstraint(name = "uk_transaction_idempotency", columnNames = "idempotency_key")
})
@Getter
@NoArgsConstructor
public class Transaction {

    @Id
    private UUID id;

    @Column(name = "idempotency_key", nullable = false, updatable = false)
    private String idempotencyKey;

    /**
     * Hash do payload da operação
     */
    private String payloadHash;

    private Instant createdAt;

    /**
     * Snapshot da resposta
     */
    private Long walletIdSnapshot;
    private BigDecimal balanceSnapshot;
    private Long versionSnapshot;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LedgerEntry> entries = new ArrayList<>();

    public Transaction(String idempotencyKey, String payloadHash) {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new DomainException("INVALID_IDEMPOTENCY_KEY");
        }

        if (payloadHash == null || payloadHash.isBlank()) {
            throw new DomainException("INVALID_PAYLOAD_HASH");
        }

        this.id = UUID.randomUUID();
        this.idempotencyKey = idempotencyKey;
        this.payloadHash = payloadHash;
        this.createdAt = Instant.now();
    }

    public void validatePayload(String incomingHash) {
        if (!this.payloadHash.equals(incomingHash)) {
            throw new DomainException("IDEMPOTENCY_PAYLOAD_MISMATCH");
        }
    }

    public void addEntry(LedgerEntry entry) {
        entry.setTransaction(this);
        this.entries.add(entry);
    }

    public void validate() {

        BigDecimal total = this.entries.stream()
                .map(LedgerEntry::getSignedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(BigDecimal.ZERO) != 0) {
            throw new DomainException("UNBALANCED_TRANSACTION");
        }
    }

    public void storeSnapshot(Long walletId, BigDecimal balance, Long version) {
        this.walletIdSnapshot = walletId;
        this.balanceSnapshot = balance;
        this.versionSnapshot = version;
    }

}