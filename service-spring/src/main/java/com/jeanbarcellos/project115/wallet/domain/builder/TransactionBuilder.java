package com.jeanbarcellos.project115.wallet.domain.builder;

import com.jeanbarcellos.project115.wallet.domain.Transaction;

public interface TransactionBuilder {

    String getOperation();

    Transaction build(TransactionContext context);
}