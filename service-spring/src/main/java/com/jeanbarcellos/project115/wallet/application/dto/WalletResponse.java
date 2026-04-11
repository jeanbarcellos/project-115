package com.jeanbarcellos.project115.wallet.application.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

/**
 * Response padrão da Wallet.
 *
 * Inclui:
 * - id
 * - saldo atual (derivado do ledger)
 * - version (para controle de concorrência via ETag)
 */
@Getter
@Builder
public class WalletResponse {

    private Long id;
    private BigDecimal balance;
    private Long version;
}