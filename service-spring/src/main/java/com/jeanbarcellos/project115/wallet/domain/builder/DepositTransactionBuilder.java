package com.jeanbarcellos.project115.wallet.domain.builder;

import static com.jeanbarcellos.project115.wallet.domain.TransactionType.*;

import org.springframework.stereotype.Component;

import com.jeanbarcellos.project115.wallet.domain.LedgerEntry;
import com.jeanbarcellos.project115.wallet.domain.Transaction;

@Component
public class DepositTransactionBuilder implements TransactionBuilder {

    private static final Long SYSTEM = 0L;

    @Override
    public String getOperation() {
        return "DEPOSIT";
    }

    @Override
    public Transaction build(TransactionContext context) {

        Transaction transaction = new Transaction(
                context.getIdempotencyKey(),
                context.getPayloadHash());

        transaction.addEntry(new LedgerEntry(
                context.getSourceWalletId(), CREDIT, context.getAmount()));

        transaction.addEntry(new LedgerEntry(
                SYSTEM, DEBIT, context.getAmount()));

        transaction.validate();

        // persistência + snapshot + resposta

        return transaction;
    }
}