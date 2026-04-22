package com.jeanbarcellos.project115.wallet.domain.aggregate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.wallet.domain.event.MoneyDepositedEvent;
import com.jeanbarcellos.project115.wallet.domain.event.MoneyWithdrawnEvent;
import com.jeanbarcellos.project115.wallet.domain.event.WalletEvent;

public class WalletAggregate {

    private Long id;
    private BigDecimal balance = BigDecimal.ZERO;

    private final List<WalletEvent> changes = new ArrayList<>();

    // ============================
    // COMMAND METHODS
    // ============================

    public void deposit(BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("INVALID_AMOUNT");
        }

        this.apply(new MoneyDepositedEvent(id, amount, Instant.now()));
    }

    public void withdraw(BigDecimal amount) {

        if (this.balance.compareTo(amount) < 0) {
            throw new DomainException("INSUFFICIENT_BALANCE");
        }

        this.apply(new MoneyWithdrawnEvent(id, amount, Instant.now()));
    }

    // ============================
    // APPLY (STATE TRANSITION)
    // ============================

    public void apply(WalletEvent event) {

        this.mutate(event);
        this.changes.add(event);
    }

    private void mutate(WalletEvent event) {

        if (event instanceof MoneyDepositedEvent e) {
            this.balance = this.balance.add(e.getAmount());
        }

        if (event instanceof MoneyWithdrawnEvent e) {
            this.balance = this.balance.subtract(e.getAmount());
        }
    }

    // ============================
    // REPLAY
    // ============================

    public void replay(List<WalletEvent> history) {
        for (WalletEvent event : history) {
            this.mutate(event);
        }
    }

    public List<WalletEvent> getChanges() {
        return changes;
    }
}