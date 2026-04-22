package com.jeanbarcellos.project115.wallet.application.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Request de transferência.
 */
@Getter
@Setter
@AllArgsConstructor
public class WalletTransferCommandRequest {

    private Long sourceWalletId;
    private Long targetWalletId;
    private BigDecimal amount;
    private Long expectedVersion;
    private String idempotencyKey;
}