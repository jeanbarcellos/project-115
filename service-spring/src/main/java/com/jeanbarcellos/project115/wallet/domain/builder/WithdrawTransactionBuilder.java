package com.jeanbarcellos.project115.wallet.domain.builder;

import static com.jeanbarcellos.project115.wallet.domain.TransactionType.CREDIT;
import static com.jeanbarcellos.project115.wallet.domain.TransactionType.DEBIT;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.wallet.domain.LedgerEntry;
import com.jeanbarcellos.project115.wallet.domain.Transaction;

@Component
public class WithdrawTransactionBuilder implements TransactionBuilder {

    private static final Long SYSTEM = 0L;

    @Override
    public String getOperation() {
        return "WITHDRAW";
    }

    @Override
    public Transaction build(TransactionContext context) {

        BigDecimal balance = context.getCurrentBalance();

        if (balance.compareTo(context.getAmount()) < 0) {
            throw new DomainException("INSUFFICIENT_BALANCE");
        }

        Transaction transaction = new Transaction(
                context.getIdempotencyKey(),
                context.getPayloadHash());

        transaction.addEntry(new LedgerEntry(
                context.getSourceWalletId(), DEBIT, context.getAmount()));
        transaction.addEntry(new LedgerEntry(
                SYSTEM, CREDIT, context.getAmount()));

        transaction.validate();

        return transaction;
    }
}