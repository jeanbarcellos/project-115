package com.jeanbarcellos.project115.wallet.application.handler;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;

import com.jeanbarcellos.core.exception.ApplicationException;
import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.application.error.WalletErrorType;
import com.jeanbarcellos.project115.wallet.application.mapper.WalletMapper;
import com.jeanbarcellos.project115.wallet.application.repository.TransactionRepository;
import com.jeanbarcellos.project115.wallet.application.repository.WalletRepository;
import com.jeanbarcellos.project115.wallet.application.translator.WalletExceptionTranslator;
import com.jeanbarcellos.project115.wallet.domain.Transaction;
import com.jeanbarcellos.project115.wallet.domain.Wallet;

/**
 * Template base para handlers.
 *
 * Contém:
 * - fluxo de idempotência
 * - persistência
 * - snapshot
 *
 * Subclasses definem apenas a lógica específica da operação.
 */
public abstract class AbstractWalletCommandHandler<T> {

    protected final WalletRepository walletRepository;
    protected final TransactionRepository transactionRepository;
    protected final WalletMapper walletMapper;

    protected AbstractWalletCommandHandler(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            WalletMapper walletMapper) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.walletMapper = walletMapper;
    }

    protected WalletResponse execute(
            Long walletId,
            BigDecimal amount,
            Long expectedVersion,
            String idempotencyKey,
            String operation) {

        String payloadHash = this.generatePayloadHash(walletId, amount, operation);

        Optional<Transaction> existing = this.transactionRepository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            existing.get().validatePayload(payloadHash);
            return this.buildFromSnapshot(existing.get());
        }

        Wallet wallet = this.walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException(WalletErrorType.WALLET_NOT_FOUND, "Wallet not found"));

        this.validateVersion(wallet, expectedVersion);

        try {

            Transaction transaction = this.doExecute(wallet, amount, payloadHash, idempotencyKey);

            this.transactionRepository.save(transaction);

            BigDecimal newBalance = this.calculateNewBalance(wallet, amount);

            wallet.updateSnapshot(newBalance);

            Wallet updated = this.walletRepository.save(wallet);

            transaction.storeSnapshot(updated.getId(), newBalance, updated.getVersion());

            return this.walletMapper.toResponse(updated, newBalance);

        } catch (DataIntegrityViolationException ex) {

            Transaction persisted = this.transactionRepository.findByIdempotencyKey(idempotencyKey)
                    .orElseThrow();

            persisted.validatePayload(payloadHash);

            return this.buildFromSnapshot(persisted);

        } catch (DomainException ex) {
            throw WalletExceptionTranslator.translate(ex);
        }
    }

    /**
     * Implementação específica da operação (deposit, withdraw, etc)
     */
    protected abstract Transaction doExecute(
            Wallet wallet,
            BigDecimal amount,
            String payloadHash,
            String idempotencyKey);

    /**
     * Cada operação define como o saldo muda
     */
    protected abstract BigDecimal calculateNewBalance(Wallet wallet, BigDecimal amount);

    protected void validateVersion(Wallet wallet, Long expectedVersion) {

        if (!wallet.getVersion().equals(expectedVersion)) {
            throw new BusinessException(WalletErrorType.IDEMPOTENT_CONFLICT, "Version mismatch");
        }
    }

    protected WalletResponse buildFromSnapshot(Transaction transaction) {
        return WalletResponse.builder()
                .id(transaction.getWalletIdSnapshot())
                .balance(transaction.getBalanceSnapshot())
                .version(transaction.getVersionSnapshot())
                .build();
    }

    protected String generatePayloadHash(Long walletId, BigDecimal amount, String operation) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    (walletId + "|" + amount + "|" + operation)
                            .getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (Exception ex) {
            throw new ApplicationException("Erro ao gerar hash", ex);
        }
    }
}