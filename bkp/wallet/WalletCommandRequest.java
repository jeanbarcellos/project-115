package com.jeanbarcellos.project115.wallet.application.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Request padrão para operações financeiras.
 */
@Getter
@Setter
@AllArgsConstructor
public class WalletCommandRequest {

    private Long walletId;
    private BigDecimal amount;
    private Long expectedVersion;
    private String idempotencyKey;
}