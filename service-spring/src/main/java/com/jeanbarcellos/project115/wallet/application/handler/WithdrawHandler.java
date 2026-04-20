package com.jeanbarcellos.project115.wallet.application.handler;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jeanbarcellos.core.exception.ApplicationException;
import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.wallet.application.command.WithdrawCommand;
import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.application.error.WalletErrorType;
import com.jeanbarcellos.project115.wallet.application.mapper.WalletMapper;
import com.jeanbarcellos.project115.wallet.application.policy.WalletPolicyEngine;
import com.jeanbarcellos.project115.wallet.application.repository.LedgerEntryRepository;
import com.jeanbarcellos.project115.wallet.application.repository.TransactionRepository;
import com.jeanbarcellos.project115.wallet.application.repository.WalletRepository;
import com.jeanbarcellos.project115.wallet.application.translator.WalletExceptionTranslator;
import com.jeanbarcellos.project115.wallet.domain.LedgerEntry;
import com.jeanbarcellos.project115.wallet.domain.LedgerService;
import com.jeanbarcellos.project115.wallet.domain.Transaction;
import com.jeanbarcellos.project115.wallet.domain.Wallet;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WithdrawHandler implements WalletCommandHandler<WithdrawCommand> {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final WalletMapper walletMapper;
    private final WalletPolicyEngine walletPolicyEngine;

    private final LedgerService ledgerService = new LedgerService();

    @Override
    public Class<WithdrawCommand> getCommandType() {
        return WithdrawCommand.class;
    }

    @Override
    @Transactional
    public WalletResponse handle(WithdrawCommand command) {

        String payloadHash = this.generatePayloadHash(command);

        Optional<Transaction> existing = this.transactionRepository.findByIdempotencyKey(command.getIdempotencyKey());

        if (existing.isPresent()) {
            existing.get().validatePayload(payloadHash);
            return this.buildFromSnapshot(existing.get());
        }

        Wallet wallet = this.walletRepository.findById(command.getWalletId())
                .orElseThrow(() -> new BusinessException(WalletErrorType.WALLET_NOT_FOUND, "Wallet not found"));

        this.validateVersion(wallet, command.getExpectedVersion());

        BigDecimal balance = this.calculateBalance(wallet.getId());

        try {

            this.walletPolicyEngine.validate(wallet, command.getAmount());

            Transaction transaction = this.ledgerService.withdraw(
                    wallet.getId(),
                    command.getAmount(),
                    balance,
                    command.getIdempotencyKey(),
                    payloadHash);

            this.transactionRepository.save(transaction);

            BigDecimal newBalance = balance.subtract(command.getAmount());

            wallet.updateSnapshot(newBalance);

            Wallet updated = this.walletRepository.save(wallet);

            transaction.storeSnapshot(updated.getId(), newBalance, updated.getVersion());

            return this.walletMapper.toResponse(updated, newBalance);

        } catch (DataIntegrityViolationException ex) {

            Transaction persisted = this.transactionRepository.findByIdempotencyKey(command.getIdempotencyKey())
                    .orElseThrow();

            persisted.validatePayload(payloadHash);

            return this.buildFromSnapshot(persisted);

        } catch (DomainException ex) {
            throw WalletExceptionTranslator.translate(ex);
        }
    }

    private BigDecimal calculateBalance(Long walletId) {

        List<LedgerEntry> entries = this.ledgerEntryRepository.findByWalletId(walletId);

        return new Wallet().calculateBalance(entries);
    }

    private void validateVersion(Wallet wallet, Long expectedVersion) {
        if (!wallet.getVersion().equals(expectedVersion)) {
            throw new BusinessException(WalletErrorType.IDEMPOTENT_CONFLICT, "Version mismatch");
        }
    }

    private WalletResponse buildFromSnapshot(Transaction transaction) {
        return WalletResponse.builder()
                .id(transaction.getWalletIdSnapshot())
                .balance(transaction.getBalanceSnapshot())
                .version(transaction.getVersionSnapshot())
                .build();
    }

    private String generatePayloadHash(WithdrawCommand command) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    (command.getWalletId() + "|" + command.getAmount() + "|WITHDRAW")
                            .getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (Exception ex) {
            throw new ApplicationException("Erro ao gerar hash", ex);
        }
    }
}