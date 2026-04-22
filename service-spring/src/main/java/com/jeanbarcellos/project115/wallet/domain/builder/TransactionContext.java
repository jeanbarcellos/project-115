package com.jeanbarcellos.project115.wallet.domain.builder;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

/**
 * Contexto imutável para construção da transação.
 */
@Getter
@Builder
public class TransactionContext {

    private Long sourceWalletId;
    private Long targetWalletId;
    private BigDecimal amount;
    private BigDecimal currentBalance;
    private String idempotencyKey;
    private String payloadHash;
}