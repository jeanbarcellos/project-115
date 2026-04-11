package com.jeanbarcellos.project115.wallet.application.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * Request para transferência entre wallets.
 */
@Getter
@Setter
public class WalletTransferRequest {

    private Long targetWalletId;
    private BigDecimal amount;
}