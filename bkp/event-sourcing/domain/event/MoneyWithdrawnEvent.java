package com.jeanbarcellos.project115.wallet.domain.event;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MoneyWithdrawnEvent implements WalletEvent {

    private Long walletId;
    private BigDecimal amount;
    private Instant occurredAt;

    @Override
    public String getType() {
        return "MONEY_WITHDRAWN";
    }
}