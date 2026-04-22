package com.jeanbarcellos.project115.wallet.application.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.exception.ApplicationException;
import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.wallet.application.cache.WalletBalanceCache;
import com.jeanbarcellos.project115.wallet.application.dto.WalletCommandRequest;
import com.jeanbarcellos.project115.wallet.application.dto.WalletCreateRequest;
import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.application.dto.WalletTransferCommandRequest;
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

/**
 * Application Service da Wallet.
 *
 * Sem abstrações desnecessárias.
 * Fluxo explícito.
 */
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    private final WalletMapper walletMapper;
    private final WalletBalanceCache walletBalanceCache;
    private final WalletPolicyEngine walletPolicyEngine;

    private final LedgerService ledgerService = new LedgerService();

    // ============================
    // QUERY
    // ============================

    public List<WalletResponse> findAll() {

        return this.walletRepository.findAll()
                .stream()
                .map(wallet -> this.walletMapper.toResponse(
                        wallet,
                        this.getBalance(wallet.getId())))
                .toList();
    }

    public WalletResponse findById(Long walletId) {

        Wallet wallet = this.findWallet(walletId);

        return this.walletMapper.toResponse(wallet, this.getBalance(walletId));
    }

    // ============================
    // CREATE
    // ============================

    @Transactional
    public WalletResponse create(WalletCreateRequest request) {

        Wallet wallet = new Wallet();

        Wallet savedWallet = this.walletRepository.save(wallet);

        if (request.getInitialBalance() != null &&
                request.getInitialBalance().compareTo(BigDecimal.ZERO) > 0) {

            String payloadHash = this.generatePayloadHash(
                    savedWallet.getId(),
                    request.getInitialBalance(),
                    "CREATE");

            Transaction transaction = this.ledgerService.deposit(
                    savedWallet.getId(),
                    request.getInitialBalance(),
                    "CREATE-" + savedWallet.getId(),
                    payloadHash);

            this.transactionRepository.save(transaction);
        }

        return this.walletMapper.toResponse(savedWallet, this.getBalance(savedWallet.getId()));
    }

    // ============================
    // DEPOSIT
    // ============================

    @Transactional
    public WalletResponse deposit(WalletCommandRequest request) {

        String payloadHash = this.generatePayloadHash(
                request.getWalletId(),
                request.getAmount(),
                "DEPOSIT");

        Optional<Transaction> existing = this.transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());

        if (existing.isPresent()) {
            existing.get().validatePayload(payloadHash);
            return this.buildResponseFromSnapshot(existing.get());
        }

        Wallet wallet = this.findWallet(request.getWalletId());

        this.validateVersion(wallet, request.getExpectedVersion());

        try {

            this.walletPolicyEngine.validate(wallet, request.getAmount());

            Transaction transaction = this.ledgerService.deposit(
                    wallet.getId(),
                    request.getAmount(),
                    request.getIdempotencyKey(),
                    payloadHash);

            this.transactionRepository.save(transaction);

            return this.finalize(wallet, transaction);

        } catch (DataIntegrityViolationException ex) {
            return this.resolveConflict(request.getIdempotencyKey(), payloadHash);
        } catch (DomainException ex) {
            throw WalletExceptionTranslator.translate(ex);
        }

    }

    // ============================
    // WITHDRAW
    // ============================

    @Transactional
    public WalletResponse withdraw(WalletCommandRequest request) {

        String payloadHash = this.generatePayloadHash(
                request.getWalletId(),
                request.getAmount(),
                "WITHDRAW");

        Optional<Transaction> existing = this.transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());

        if (existing.isPresent()) {
            existing.get().validatePayload(payloadHash);
            return this.buildResponseFromSnapshot(existing.get());
        }

        Wallet wallet = this.findWallet(request.getWalletId());

        this.validateVersion(wallet, request.getExpectedVersion());

        BigDecimal balance = this.getBalance(wallet.getId());

        try {

            this.walletPolicyEngine.validate(wallet, request.getAmount());

            Transaction transaction = this.ledgerService.withdraw(
                    wallet.getId(),
                    request.getAmount(),
                    balance,
                    request.getIdempotencyKey(),
                    payloadHash);

            this.transactionRepository.save(transaction);

            return this.finalize(wallet, transaction);

        } catch (DataIntegrityViolationException ex) {
            return this.resolveConflict(request.getIdempotencyKey(), payloadHash);
        } catch (DomainException ex) {
            throw WalletExceptionTranslator.translate(ex);
        }
    }

    // ============================
    // TRANSFER
    // ============================

    @Transactional
    public WalletResponse transfer(WalletTransferCommandRequest request) {

        String payloadHash = this.generatePayloadHash(
                request.getSourceWalletId(),
                request.getAmount(),
                "TRANSFER");

        Optional<Transaction> existing = this.transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());

        if (existing.isPresent()) {
            existing.get().validatePayload(payloadHash);
            return this.buildResponseFromSnapshot(existing.get());
        }

        Wallet source = this.findWallet(request.getSourceWalletId());
        Wallet target = this.findWallet(request.getTargetWalletId());

        this.validateVersion(source, request.getExpectedVersion());

        BigDecimal balance = this.getBalance(source.getId());

        try {

            this.walletPolicyEngine.validate(source, request.getAmount());

            Transaction transaction = this.ledgerService.transfer(
                    source.getId(),
                    target.getId(),
                    request.getAmount(),
                    balance,
                    request.getIdempotencyKey(),
                    payloadHash);

            this.transactionRepository.save(transaction);

            return this.finalize(source, transaction);

        } catch (DataIntegrityViolationException ex) {
            return this.resolveConflict(request.getIdempotencyKey(), payloadHash);
        } catch (DomainException ex) {
            throw WalletExceptionTranslator.translate(ex);
        }
    }

    // ============================
    // BALANCE
    // ============================

    public WalletResponse getBalanceById(Long walletId) {

        Wallet wallet = this.findWallet(walletId);

        BigDecimal balance = this.getBalance(walletId);

        return this.walletMapper.toResponse(wallet, balance);
    }

    // ============================
    // HELPERS
    // ============================

    private WalletResponse finalize(Wallet wallet, Transaction transaction) {

        this.walletBalanceCache.evict(wallet.getId());

        BigDecimal newBalance = this.getBalance(wallet.getId());

        wallet.updateSnapshot(newBalance);

        Wallet updated = this.walletRepository.save(wallet);

        transaction.storeSnapshot(
                updated.getId(),
                newBalance,
                updated.getVersion()
        );

        return this.walletMapper.toResponse(updated, newBalance);
    }

    private WalletResponse resolveConflict(String key, String hash) {

        Transaction transaction =
                this.transactionRepository.findByIdempotencyKey(key)
                        .orElseThrow();

        transaction.validatePayload(hash);

        return this.buildResponseFromSnapshot(transaction);
    }

    private WalletResponse buildResponseFromSnapshot(Transaction transaction) {

        return WalletResponse.builder()
                .id(transaction.getWalletIdSnapshot())
                .balance(transaction.getBalanceSnapshot())
                .version(transaction.getVersionSnapshot())
                .build();
    }

    private Wallet findWallet(Long walletId) {

        return this.walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException(
                        WalletErrorType.WALLET_NOT_FOUND,
                        "Wallet not found"));
    }

    private void validateVersion(Wallet wallet, Long expectedVersion) {

        if (expectedVersion == null) {
            throw new BusinessException(
                    TechnicalErrorType.INVALID_PARAMETER,
                    "Missing If-Match header");
        }

        if (!wallet.getVersion().equals(expectedVersion)) {
            throw new BusinessException(
                    WalletErrorType.IDEMPOTENT_CONFLICT,
                    "Version mismatch"
            );
        }
    }

    private BigDecimal getBalance(Long walletId) {

        BigDecimal cached = this.walletBalanceCache.get(walletId);

        if (cached != null) {
            return cached;
        }

        List<LedgerEntry> entries = this.ledgerEntryRepository.findByWalletId(walletId);

        BigDecimal balance = new Wallet().calculateBalance(entries);

        this.walletBalanceCache.put(walletId, balance);

        return balance;
    }

    private String generatePayloadHash(Long walletId, BigDecimal amount, String operation) {

        try {
            String raw = walletId + "|" + amount + "|" + operation;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new ApplicationException("Erro ao gerar hash", exception);
        }
    }

}