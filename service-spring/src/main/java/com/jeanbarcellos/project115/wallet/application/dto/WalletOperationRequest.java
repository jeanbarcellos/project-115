package com.jeanbarcellos.project115.wallet.application.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletOperationRequest {

    private BigDecimal amount;
}