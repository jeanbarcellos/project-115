package com.jeanbarcellos.project115.wallet.domain.event;

import java.time.Instant;

public interface WalletEvent {

    Long getWalletId();

    Instant getOccurredAt();

    String getType();
}