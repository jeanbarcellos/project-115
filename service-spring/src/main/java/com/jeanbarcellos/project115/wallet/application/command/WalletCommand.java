package com.jeanbarcellos.project115.wallet.application.command;

import java.math.BigDecimal;

/**
 * Comando base fechado (sealed).
 * Garante controle total dos tipos permitidos.
 */
public sealed interface WalletCommand
        permits DepositCommand, WithdrawCommand, TransferCommand {

    Long getWalletId();

    BigDecimal getAmount();

    Long getExpectedVersion();

    String getIdempotencyKey();

    String getOperation(); // importante para hash
}