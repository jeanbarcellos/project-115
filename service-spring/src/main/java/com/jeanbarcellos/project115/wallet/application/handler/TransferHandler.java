package com.jeanbarcellos.project115.wallet.application.handler;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.jeanbarcellos.project115.wallet.application.command.TransferCommand;
import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.application.mapper.WalletMapper;
import com.jeanbarcellos.project115.wallet.application.repository.TransactionRepository;
import com.jeanbarcellos.project115.wallet.application.repository.WalletRepository;
import com.jeanbarcellos.project115.wallet.domain.LedgerService;
import com.jeanbarcellos.project115.wallet.domain.Transaction;
import com.jeanbarcellos.project115.wallet.domain.Wallet;

@Component
public class TransferHandler extends AbstractWalletCommandHandler<TransferCommand>
        implements WalletCommandHandler<TransferCommand> {

    private final LedgerService ledgerService = new LedgerService();
    private final WalletRepository walletRepository;

    public TransferHandler(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            WalletMapper walletMapper) {
        super(walletRepository, transactionRepository, walletMapper);
        this.walletRepository = walletRepository;
    }

    @Override
    public Class<TransferCommand> getCommandType() {
        return TransferCommand.class;
    }

    @Override
    public WalletResponse handle(TransferCommand command) {

        return this.execute(
                command.getWalletId(),
                command.getAmount(),
                command.getExpectedVersion(),
                command.getIdempotencyKey(),
                "TRANSFER");
    }

    @Override
    protected Transaction doExecute(
            Wallet wallet,
            BigDecimal amount,
            String payloadHash,
            String idempotencyKey) {

        TransferCommand command = null;// Teste

        Wallet target = this.walletRepository.findById(
                ((TransferCommand) command).getTargetWalletId()).orElseThrow();

        return this.ledgerService.transfer(
                wallet.getId(),
                target.getId(),
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