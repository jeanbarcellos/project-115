package com.jeanbarcellos.project115.wallet.domain.builder;

import static com.jeanbarcellos.project115.wallet.domain.TransactionType.CREDIT;
import static com.jeanbarcellos.project115.wallet.domain.TransactionType.DEBIT;

import org.springframework.stereotype.Component;

import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.wallet.domain.LedgerEntry;
import com.jeanbarcellos.project115.wallet.domain.Transaction;

@Component
public class TransferTransactionBuilder implements TransactionBuilder {

    private static final Long SYSTEM = 0L;

    @Override
    public String getOperation() {
        return "TRANSFER";
    }

    @Override
    public Transaction build(TransactionContext context) {

        if (context.getSourceWalletId().equals(context.getTargetWalletId())) {
            throw new DomainException("INVALID_TRANSFER");
        }

        if (context.getCurrentBalance().compareTo(context.getAmount()) < 0) {
            throw new DomainException("INSUFFICIENT_BALANCE");
        }

        Transaction transaction = new Transaction(
                context.getIdempotencyKey(),
                context.getPayloadHash()
        );

        transaction.addEntry(new LedgerEntry(
                context.getSourceWalletId(), DEBIT, context.getAmount()));

        transaction.addEntry(new LedgerEntry(
                context.getTargetWalletId(), CREDIT, context.getAmount()));

        transaction.addEntry(new LedgerEntry(SYSTEM, CREDIT, context.getAmount()));
        transaction.addEntry(new LedgerEntry(SYSTEM, DEBIT, context.getAmount()));

        transaction.validate();

        return transaction;
    }
}