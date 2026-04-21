package com.jeanbarcellos.project115.wallet.application.handler;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.jeanbarcellos.project115.wallet.application.command.DepositCommand;
import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.application.mapper.WalletMapper;
import com.jeanbarcellos.project115.wallet.application.repository.TransactionRepository;
import com.jeanbarcellos.project115.wallet.application.repository.WalletRepository;
import com.jeanbarcellos.project115.wallet.domain.LedgerService;
import com.jeanbarcellos.project115.wallet.domain.Transaction;
import com.jeanbarcellos.project115.wallet.domain.Wallet;

@Component
public class DepositHandler extends AbstractWalletCommandHandler<DepositCommand>
        implements WalletCommandHandler<DepositCommand> {

    private final LedgerService ledgerService = new LedgerService();

    public DepositHandler(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            WalletMapper walletMapper) {
        super(walletRepository, transactionRepository, walletMapper);
    }

    @Override
    public Class<DepositCommand> getCommandType() {
        return DepositCommand.class;
    }

    @Override
    public WalletResponse handle(DepositCommand command) {

        return this.execute(
                command.getWalletId(),
                command.getAmount(),
                command.getExpectedVersion(),
                command.getIdempotencyKey(),
                "DEPOSIT");
    }

    @Override
    protected Transaction doExecute(
            Wallet wallet,
            BigDecimal amount,
            String payloadHash,
            String idempotencyKey) {

        return this.ledgerService.deposit(
                wallet.getId(),
                amount,
                idempotencyKey,
                payloadHash);
    }

    @Override
    protected BigDecimal calculateNewBalance(Wallet wallet, BigDecimal amount) {
        return wallet.getBalanceSnapshot().add(amount);
    }
}