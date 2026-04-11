package com.jeanbarcellos.project115.wallet.domain;

import com.jeanbarcellos.core.exception.DomainException;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Aggregate raiz Wallet.
 */
@Getter
public class Wallet {

    @Setter
    private Long id;
    private BigDecimal balance;
    private Long version; // controle de conorrencia

    public Wallet(BigDecimal initialBalance) {

        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException(
                    "Initial balance cannot be negative",
                    Map.of("balance", initialBalance));
        }

        this.balance = initialBalance;
        this.version = 0L;
    }

    public void deposit(BigDecimal amount) {

        validateAmount(amount);

        this.balance = this.balance.add(amount);
        this.version++;
    }

    public void withdraw(BigDecimal amount) {

        this.validateAmount(amount);

        if (this.balance.compareTo(amount) < 0) {
            throw new DomainException(
                    "Insufficient balance",
                    Map.of("balance", balance,
                            "requested", amount));
        }

        this.balance = this.balance.subtract(amount);
        this.version++;
    }

    private void validateAmount(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException(
                    "Amount must be greater than zero",
                    Map.of("amount", amount));
        }
    }

}