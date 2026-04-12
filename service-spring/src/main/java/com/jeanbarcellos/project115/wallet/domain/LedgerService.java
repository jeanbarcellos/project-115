package com.jeanbarcellos.project115.wallet.domain;

import java.math.BigDecimal;

import com.jeanbarcellos.core.exception.DomainException;

/**
 * Serviço de domínio responsável por criar transações válidas.
 */
public class LedgerService {

    private static final Long SYSTEM = 0L;

    public Transaction deposit(Long walletId, BigDecimal amount, String idempotencyKey, String payloadHash) {

        this.validateAmount(amount);

        Transaction transaction = new Transaction(idempotencyKey, payloadHash);

        transaction.addEntry(new LedgerEntry(walletId, TransactionType.CREDIT, amount));
        transaction.addEntry(new LedgerEntry(SYSTEM, TransactionType.DEBIT, amount));

        transaction.validate();

        return transaction;
    }

    public Transaction withdraw(Long walletId, BigDecimal amount, BigDecimal balance, String idempotencyKey,
            String payloadHash) {

        this.validateAmount(amount);

        if (balance.compareTo(amount) < 0) {
            throw new DomainException("INSUFFICIENT_BALANCE");
        }

        Transaction transaction = new Transaction(idempotencyKey, payloadHash);

        transaction.addEntry(new LedgerEntry(walletId, TransactionType.DEBIT, amount));
        transaction.addEntry(new LedgerEntry(SYSTEM, TransactionType.CREDIT, amount));

        transaction.validate();

        return transaction;
    }

    public Transaction transfer(Long from, Long to, BigDecimal amount, BigDecimal balance, String idempotencyKey,
            String payloadHash) {

        this.validateAmount(amount);

        if (from.equals(to)) {
            throw new DomainException("INVALID_TRANSFER");
        }

        if (balance.compareTo(amount) < 0) {
            throw new DomainException("INSUFFICIENT_BALANCE");
        }

        Transaction transaction = new Transaction(idempotencyKey, payloadHash);

        transaction.addEntry(new LedgerEntry(from, TransactionType.DEBIT, amount));
        transaction.addEntry(new LedgerEntry(to, TransactionType.CREDIT, amount));

        transaction.addEntry(new LedgerEntry(SYSTEM, TransactionType.CREDIT, amount));
        transaction.addEntry(new LedgerEntry(SYSTEM, TransactionType.DEBIT, amount));

        transaction.validate();

        return transaction;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("INVALID_AMOUNT");
        }
    }
}