package com.jeanbarcellos.project115.wallet.application.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.wallet.application.cache.WalletBalanceCache;
import com.jeanbarcellos.project115.wallet.application.dto.WalletCreateRequest;
import com.jeanbarcellos.project115.wallet.application.dto.WalletOperationRequest;
import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.application.dto.WalletTransferRequest;
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
 * Serviço de aplicação da Wallet.
 *
 * Responsável por:
 * - orquestrar operações financeiras
 * - garantir idempotência forte
 * - validar concorrência (ETag)
 * - integrar antifraude
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

        BigDecimal balance = this.getBalance(walletId);

        return this.walletMapper.toResponse(wallet, balance);
    }

    // ============================
    // CREATE
    // ============================

    @Transactional
    public WalletResponse create(WalletCreateRequest request) {

        Wallet wallet = new Wallet();

        Wallet savedWallet = this.walletRepository.save(wallet);

        BigDecimal initialBalance = BigDecimal.ZERO;

        if (request.getInitialBalance() != null &&
                request.getInitialBalance().compareTo(BigDecimal.ZERO) > 0) {

            String payloadHash = this.generatePayloadHash(
                    savedWallet.getId(),
                    request.getInitialBalance(),
                    "INITIAL");

            Transaction transaction = this.ledgerService.deposit(
                    savedWallet.getId(),
                    request.getInitialBalance(),
                    "INITIAL-" + savedWallet.getId(),
                    payloadHash);

            this.transactionRepository.save(transaction);

            initialBalance = request.getInitialBalance();
        }

        return this.walletMapper.toResponse(savedWallet, initialBalance);
    }

    // ============================
    // DEPOSIT
    // ============================

    @Transactional
    public WalletResponse deposit(
            Long walletId,
            WalletOperationRequest request,
            Long expectedVersion,
            String idempotencyKey) {

        return this.executeOperation(
                walletId,
                request.getAmount(),
                expectedVersion,
                idempotencyKey,
                "DEPOSIT",
                (wallet, amount, balance, key, hash) -> this.ledgerService.deposit(wallet.getId(), amount, key, hash));
    }

    // ============================
    // WITHDRAW
    // ============================

    @Transactional
    public WalletResponse withdraw(
            Long walletId,
            WalletOperationRequest request,
            Long expectedVersion,
            String idempotencyKey) {

        return this.executeOperation(
                walletId,
                request.getAmount(),
                expectedVersion,
                idempotencyKey,
                "WITHDRAW",
                (wallet, amount, balance, key, hash) -> this.ledgerService.withdraw(wallet.getId(), amount, balance,
                        key, hash));
    }

    // ============================
    // TRANSFER
    // ============================

    @Transactional
    public WalletResponse transfer(
            Long walletId,
            WalletTransferRequest request,
            Long expectedVersion,
            String idempotencyKey) {

        Wallet targetWallet = this.findWallet(request.getTargetWalletId());

        return this.executeOperation(
                walletId,
                request.getAmount(),
                expectedVersion,
                idempotencyKey,
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

    public WalletResponse getBalanceById(Long id) {

        Wallet wallet = this.findWallet(id);

        BigDecimal balance = this.getBalance(id);

        return this.walletMapper.toResponse(wallet, balance);
    }

    // ============================
    // CORE FLOW (sem abstração feia)
    // ============================

    private WalletResponse executeOperation(
            Long walletId,
            BigDecimal amount,
            Long expectedVersion,
            String idempotencyKey,
            String operation,
            OperationExecutor executor) {

        String payloadHash = this.generatePayloadHash(walletId, amount, operation);

        Optional<Transaction> existing = this.transactionRepository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {

            Transaction existingTransaction = existing.get();

            existingTransaction.validatePayload(payloadHash);

            return this.buildResponseFromSnapshot(existingTransaction);
        }

        Wallet wallet = this.findWallet(walletId);

        this.validateVersion(wallet, expectedVersion);

        BigDecimal balance = this.getBalance(walletId);

        try {

            this.antiFraudService.validate(
                    amount,
                    this.ledgerEntryRepository.findByWalletId(walletId));

            Transaction transaction = executor.execute(
                    wallet,
                    amount,
                    balance,
                    idempotencyKey,
                    payloadHash);

            this.transactionRepository.save(transaction);

            this.walletBalanceCache.evict(walletId);

            BigDecimal newBalance = this.getBalance(walletId);

            wallet.updateSnapshot(newBalance);

            Wallet updatedWallet = this.walletRepository.save(wallet);

            transaction.storeSnapshot(
                    updatedWallet.getId(),
                    newBalance,
                    updatedWallet.getVersion());

            return this.walletMapper.toResponse(updatedWallet, newBalance);

        } catch (DataIntegrityViolationException exception) {

            Transaction persisted = this.transactionRepository.findByIdempotencyKey(idempotencyKey)
                    .orElseThrow(() -> new BusinessException(
                            WalletErrorType.IDEMPOTENT_CONFLICT,
                            "Idempotent transaction not found"));

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

        String raw = walletId + "|" + amount + "|" + operation;

        return Integer.toHexString(raw.hashCode());
    }

    @FunctionalInterface
    private interface OperationExecutor {
        Transaction execute(Wallet wallet, BigDecimal amount, BigDecimal balance, String key, String hash);
    }
}