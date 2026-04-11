package com.jeanbarcellos.project115.wallet.domain;

import java.math.BigDecimal;
import java.util.List;

import com.jeanbarcellos.core.exception.DomainException;

public class LedgerService {

    private static final Long SYSTEM = 0L;

    public Transaction deposit(Long walletId, BigDecimal amount) {

        validate(amount);

        return new Transaction(List.of(
                new LedgerEntry(walletId, TransactionType.CREDIT, amount),
                new LedgerEntry(SYSTEM, TransactionType.DEBIT, amount)));
    }

    public Transaction withdraw(Long walletId, BigDecimal amount, BigDecimal balance) {

        validate(amount);

        if (balance.compareTo(amount) < 0) {
            throw new DomainException("INSUFFICIENT_BALANCE");
        }

        return new Transaction(List.of(
                new LedgerEntry(walletId, TransactionType.DEBIT, amount),
                new LedgerEntry(SYSTEM, TransactionType.CREDIT, amount)));
    }

    public Transaction transfer(Long from, Long to, BigDecimal amount, BigDecimal balance) {

        validate(amount);

        if (from.equals(to)) {
            throw new DomainException("INVALID_TRANSFER");
        }

        if (balance.compareTo(amount) < 0) {
            throw new DomainException("INSUFFICIENT_BALANCE");
        }

        return new Transaction(List.of(
                new LedgerEntry(from, TransactionType.DEBIT, amount),
                new LedgerEntry(to, TransactionType.CREDIT, amount),
                new LedgerEntry(SYSTEM, TransactionType.CREDIT, amount),
                new LedgerEntry(SYSTEM, TransactionType.DEBIT, amount)));
    }

    private void validate(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("INVALID_AMOUNT");
        }
    }
}