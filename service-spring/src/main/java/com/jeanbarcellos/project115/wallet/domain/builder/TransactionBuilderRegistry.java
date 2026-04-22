package com.jeanbarcellos.project115.wallet.domain.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class TransactionBuilderRegistry {

    private final Map<String, TransactionBuilder> builders = new HashMap<>();

    public TransactionBuilderRegistry(List<TransactionBuilder> builderList) {

        for (TransactionBuilder builder : builderList) {
            this.builders.put(builder.getOperation(), builder);
        }
    }

    public TransactionBuilder get(String operation) {

        TransactionBuilder builder = this.builders.get(operation);

        if (builder == null) {
            throw new IllegalArgumentException("No builder for operation: " + operation);
        }

        return builder;
    }
}