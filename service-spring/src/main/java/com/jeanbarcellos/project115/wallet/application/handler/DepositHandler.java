package com.jeanbarcellos.project115.wallet.application.handler;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.jeanbarcellos.core.exception.ApplicationException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.wallet.application.command.DepositCommand;
import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.application.mapper.WalletMapper;
import com.jeanbarcellos.project115.wallet.application.policy.WalletPolicyEngine;
import com.jeanbarcellos.project115.wallet.application.repository.TransactionRepository;
import com.jeanbarcellos.project115.wallet.application.repository.WalletRepository;
import com.jeanbarcellos.project115.wallet.application.translator.WalletExceptionTranslator;
import com.jeanbarcellos.project115.wallet.domain.LedgerService;
import com.jeanbarcellos.project115.wallet.domain.Transaction;
import com.jeanbarcellos.project115.wallet.domain.Wallet;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DepositHandler implements WalletCommandHandler<DepositCommand> {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletMapper walletMapper;
    private final WalletPolicyEngine policyEngine;

    private final LedgerService ledgerService = new LedgerService();

    @Override
    public Class<DepositCommand> getCommandType() {
        return DepositCommand.class;
    }

    @Override
    public WalletResponse handle(DepositCommand command) {

        String hash = this.hash(command);

        Optional<Transaction> existing = this.transactionRepository.findByIdempotencyKey(command.getIdempotencyKey());

        if (existing.isPresent()) {
            existing.get().validatePayload(hash);
            return this.toResponse(existing.get());
        }

        Wallet wallet = this.walletRepository.findById(command.getWalletId())
                .orElseThrow();

        try {

            this.policyEngine.validate(wallet, command.getAmount());

            Transaction transaction = this.ledgerService.deposit(
                    wallet.getId(),
                    command.getAmount(),
                    command.getIdempotencyKey(),
                    hash);

            this.transactionRepository.save(transaction);

            BigDecimal balance = wallet.getBalanceSnapshot().add(command.getAmount());

            wallet.updateSnapshot(balance);

            Wallet updated = this.walletRepository.save(wallet);

            transaction.storeSnapshot(updated.getId(), balance, updated.getVersion());

            return this.walletMapper.toResponse(updated, balance);

        } catch (DataIntegrityViolationException ex) {

            Transaction persisted = this.transactionRepository.findByIdempotencyKey(command.getIdempotencyKey())
                    .orElseThrow();

            persisted.validatePayload(hash);

            return this.toResponse(persisted);

        } catch (DomainException ex) {
            throw WalletExceptionTranslator.translate(ex);
        }
    }

    private WalletResponse toResponse(Transaction tx) {
        return WalletResponse.builder()
                .id(tx.getWalletIdSnapshot())
                .balance(tx.getBalanceSnapshot())
                .version(tx.getVersionSnapshot())
                .build();
    }

    private String hash(DepositCommand command) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] bytes = digest.digest(
                    (command.getWalletId() + "|" + command.getAmount() + "|DEPOSIT")
                            .getBytes());

            return HexFormat.of().formatHex(bytes);

        } catch (Exception ex) {
            throw new ApplicationException("Erro ao gerar hash", ex);
        }
    }
}