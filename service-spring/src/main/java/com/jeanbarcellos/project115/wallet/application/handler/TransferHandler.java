package com.jeanbarcellos.project115.wallet.application.handler;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jeanbarcellos.core.exception.ApplicationException;
import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.wallet.application.command.TransferCommand;
import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.application.error.WalletErrorType;
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
public class TransferHandler implements WalletCommandHandler<TransferCommand> {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletMapper walletMapper;
    private final WalletPolicyEngine walletPolicyEngine;

    private final LedgerService ledgerService = new LedgerService();

    @Override
    public Class<TransferCommand> getCommandType() {
        return TransferCommand.class;
    }

    @Override
    @Transactional
    public WalletResponse handle(TransferCommand command) {

        String payloadHash = this.generatePayloadHash(command);

        Optional<Transaction> existing =
                this.transactionRepository.findByIdempotencyKey(command.getIdempotencyKey());

        if (existing.isPresent()) {
            existing.get().validatePayload(payloadHash);
            return this.buildFromSnapshot(existing.get());
        }

        Wallet source = this.walletRepository.findById(command.getWalletId())
                .orElseThrow(() ->
                        new BusinessException(WalletErrorType.WALLET_NOT_FOUND, "Source wallet not found"));

        Wallet target = this.walletRepository.findById(command.getTargetWalletId())
                .orElseThrow(() ->
                        new BusinessException(WalletErrorType.WALLET_NOT_FOUND, "Target wallet not found"));

        this.validateVersion(source, command.getExpectedVersion());

        try {

            this.walletPolicyEngine.validate(source, command.getAmount());

            Transaction transaction = this.ledgerService.transfer(
                    source.getId(),
                    target.getId(),
                    command.getAmount(),
                    source.getBalanceSnapshot(),
                    command.getIdempotencyKey(),
                    payloadHash
            );

            this.transactionRepository.save(transaction);

            BigDecimal newBalance = source.getBalanceSnapshot().subtract(command.getAmount());

            source.updateSnapshot(newBalance);

            Wallet updated = this.walletRepository.save(source);

            transaction.storeSnapshot(updated.getId(), newBalance, updated.getVersion());

            return this.walletMapper.toResponse(updated, newBalance);

        } catch (DataIntegrityViolationException ex) {

            Transaction persisted =
                    this.transactionRepository.findByIdempotencyKey(command.getIdempotencyKey())
                            .orElseThrow();

            persisted.validatePayload(payloadHash);

            return this.buildFromSnapshot(persisted);

        } catch (DomainException ex) {
            throw WalletExceptionTranslator.translate(ex);
        }
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

    private String generatePayloadHash(TransferCommand command) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    (command.getWalletId() + "|" + command.getAmount() + "|TRANSFER")
                            .getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception ex) {
            throw new ApplicationException("Erro ao gerar hash", ex);
        }
    }
}