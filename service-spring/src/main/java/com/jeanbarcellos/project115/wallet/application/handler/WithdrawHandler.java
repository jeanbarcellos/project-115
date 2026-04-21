package com.jeanbarcellos.project115.wallet.application.handler;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.jeanbarcellos.project115.wallet.application.command.WithdrawCommand;
import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.application.mapper.WalletMapper;
import com.jeanbarcellos.project115.wallet.application.repository.TransactionRepository;
import com.jeanbarcellos.project115.wallet.application.repository.WalletRepository;
import com.jeanbarcellos.project115.wallet.domain.LedgerService;
import com.jeanbarcellos.project115.wallet.domain.Transaction;
import com.jeanbarcellos.project115.wallet.domain.Wallet;

@Component
public class WithdrawHandler extends AbstractWalletCommandHandler<WithdrawCommand>
        implements WalletCommandHandler<WithdrawCommand> {

    private final LedgerService ledgerService = new LedgerService();

    public WithdrawHandler(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            WalletMapper walletMapper) {
        super(walletRepository, transactionRepository, walletMapper);
    }

    @Override
    public Class<WithdrawCommand> getCommandType() {
        return WithdrawCommand.class;
    }

    @Override
    public WalletResponse handle(WithdrawCommand command) {

        return this.execute(
                command.getWalletId(),
                command.getAmount(),
                command.getExpectedVersion(),
                command.getIdempotencyKey(),
                "WITHDRAW");
    }

    @Override
    protected Transaction doExecute(
            Wallet wallet,
            BigDecimal amount,
            String payloadHash,
            String idempotencyKey) {

        return this.ledgerService.withdraw(
                wallet.getId(),
                amount,
                wallet.getBalanceSnapshot(),
                idempotencyKey,
                payloadHash);
    }

    @Override
    protected BigDecimal calculateNewBalance(Wallet wallet, BigDecimal amount) {
        return wallet.getBalanceSnapshot().subtract(amount);
    }
}