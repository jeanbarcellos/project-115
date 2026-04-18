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
import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.wallet.application.cache.WalletBalanceCache;
import com.jeanbarcellos.project115.wallet.application.dto.WalletCommandRequest;
import com.jeanbarcellos.project115.wallet.application.dto.WalletCreateRequest;
import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.application.dto.WalletTransferCommandRequest;
import com.jeanbarcellos.project115.wallet.application.error.WalletErrorType;
import com.jeanbarcellos.project115.wallet.application.mapper.WalletMapper;
import com.jeanbarcellos.project115.wallet.application.repository.LedgerEntryRepository;
import com.jeanbarcellos.project115.wallet.application.repository.TransactionRepository;
import com.jeanbarcellos.project115.wallet.application.repository.WalletRepository;
import com.jeanbarcellos.project115.wallet.application.translator.WalletExceptionTranslator;
import com.jeanbarcellos.project115.wallet.domain.AntiFraudService;
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

    private final LedgerService ledgerService = new LedgerService();
    private final AntiFraudService antiFraudService = new AntiFraudService();

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

        return this.processFinancialOperation(
                request,
                "DEPOSIT",
                (wallet, amount, balance, key, hash) -> this.ledgerService.deposit(wallet.getId(), amount, key, hash));
    }

    // ============================
    // WITHDRAW
    // ============================

    @Transactional
    public WalletResponse withdraw(WalletCommandRequest request) {

        return this.processFinancialOperation(
                request,
                "WITHDRAW",
                (wallet, amount, balance, key, hash) -> {

                    this.antiFraudService.validate(
                            amount,
                            this.ledgerEntryRepository.findByWalletId(wallet.getId()));

                    return this.ledgerService.withdraw(
                            wallet.getId(),
                            amount,
                            balance,
                            key,
                            hash);
                });
    }

    // ============================
    // TRANSFER
    // ============================

    @Transactional
    public WalletResponse transfer(WalletTransferCommandRequest request) {

        Wallet targetWallet = this.findWallet(request.getTargetWalletId());

        return this.processFinancialOperation(
                request.getSourceWalletId(),
                request.getAmount(),
                request.getExpectedVersion(),
                request.getIdempotencyKey(),
                "TRANSFER",
                (wallet, amount, balance, key, hash) -> this.ledgerService.transfer(
                        wallet.getId(),
                        targetWallet.getId(),
                        amount,
                        balance,
                        key,
                        hash));
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
    // CORE (ÚNICO PONTO)
    // ============================

    private WalletResponse processFinancialOperation(
            WalletCommandRequest request,
            String operation,
            OperationHandler handler) {

        return this.processFinancialOperation(
                request.getWalletId(),
                request.getAmount(),
                request.getExpectedVersion(),
                request.getIdempotencyKey(),
                operation,
                handler);
    }

    private WalletResponse processFinancialOperation(
            Long walletId,
            BigDecimal amount,
            Long expectedVersion,
            String idempotencyKey,
            String operation,
            OperationHandler handler) {

        String payloadHash = this.generatePayloadHash(walletId, amount, operation);

        Optional<Transaction> existing = this.transactionRepository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            existing.get().validatePayload(payloadHash);
            return this.buildResponseFromSnapshot(existing.get());
        }

        Wallet wallet = this.findWallet(walletId);

        this.validateVersion(wallet, expectedVersion);

        BigDecimal balance = this.getBalance(walletId);

        try {

            Transaction transaction = handler.execute(
                    wallet,
                    amount,
                    balance,
                    idempotencyKey,
                    payloadHash);

            this.transactionRepository.save(transaction);

            this.walletBalanceCache.evict(walletId);

            BigDecimal newBalance = this.getBalance(walletId);

            wallet.updateSnapshot(newBalance);

            Wallet updated = this.walletRepository.save(wallet);

            transaction.storeSnapshot(
                    updated.getId(),
                    newBalance,
                    updated.getVersion());

            return this.walletMapper.toResponse(updated, newBalance);

        } catch (DataIntegrityViolationException exception) {

            Transaction persisted = this.transactionRepository.findByIdempotencyKey(idempotencyKey)
                    .orElseThrow(() -> new BusinessException(
                            WalletErrorType.IDEMPOTENT_CONFLICT,
                            "Transaction not found"));

            persisted.validatePayload(payloadHash);

            return this.buildResponseFromSnapshot(persisted);

        } catch (DomainException exception) {
            throw WalletExceptionTranslator.translate(exception);
        }
    }

    // ============================
    // HELPERS
    // ============================

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
                    TechnicalErrorType.CONFLICT,
                    "Version mismatch");
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
            throw new RuntimeException(exception);
        }
    }

    @FunctionalInterface
    private interface OperationHandler {
        Transaction execute(Wallet wallet, BigDecimal amount, BigDecimal balance, String key, String hash);
    }
}