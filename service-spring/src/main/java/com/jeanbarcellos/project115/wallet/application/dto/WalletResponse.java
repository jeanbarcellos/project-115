package com.jeanbarcellos.project115.wallet.application.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WalletResponse {

    private Long id;
    private BigDecimal balance;
}