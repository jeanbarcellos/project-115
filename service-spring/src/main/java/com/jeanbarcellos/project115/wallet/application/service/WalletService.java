package com.jeanbarcellos.project115.wallet.application.service;

import java.math.BigDecimal;
import java.util.List;

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
import com.jeanbarcellos.project115.wallet.application.repository.LedgerRepository;
import com.jeanbarcellos.project115.wallet.application.repository.WalletRepository;
import com.jeanbarcellos.project115.wallet.application.translator.WalletExceptionTranslator;
import com.jeanbarcellos.project115.wallet.domain.AntiFraudService;
import com.jeanbarcellos.project115.wallet.domain.LedgerEntry;
import com.jeanbarcellos.project115.wallet.domain.LedgerService;
import com.jeanbarcellos.project115.wallet.domain.Transaction;
import com.jeanbarcellos.project115.wallet.domain.Wallet;

import lombok.RequiredArgsConstructor;

/**
 * Serviço de aplicação responsável por orquestrar as operações da Wallet.
 *
 * <p>
 * Responsabilidades:
 * <ul>
 *     <li>Orquestrar operações de negócio (deposit, withdraw, transfer)</li>
 *     <li>Aplicar validação de concorrência (ETag / If-Match)</li>
 *     <li>Interagir com o domínio (LedgerService, AntiFraudService)</li>
 *     <li>Gerenciar cache de saldo</li>
 *     <li>Traduzir exceções de domínio para exceções de aplicação</li>
 * </ul>
 * </p>
 *
 * <p>
 * Regras importantes:
 * <ul>
 *     <li>Saldo nunca é persistido diretamente (derivado do ledger)</li>
 *     <li>Todas as operações financeiras geram transações (double-entry)</li>
 *     <li>Controle de concorrência obrigatório via versão</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final LedgerRepository ledgerRepository;

    private final WalletMapper mapper;
    private final WalletBalanceCache cache;

    private final LedgerService ledgerService = new LedgerService();
    private final AntiFraudService antiFraudService = new AntiFraudService();

    // ============================
    // QUERY
    // ============================

    public List<WalletResponse> findAll() {
        return this.walletRepository.findAll()
                .stream()
                .map(wallet -> this.mapper.toResponse(wallet, this.getBalance(wallet.getId())))
                .toList();
    }

    public WalletResponse findById(Long id) {

        Wallet wallet = this.findWallet(id);
        BigDecimal balance = this.getBalance(id);

        return this.mapper.toResponse(wallet, balance);
    }

    // ============================
    // CREATE
    // ============================

    @Transactional
    public WalletResponse create(WalletCreateRequest request) {

        Wallet wallet = new Wallet();

        Wallet saved = this.walletRepository.save(wallet);

        // ⚠️ opcional: gerar transação inicial
        if (request.getInitialBalance() != null &&
            request.getInitialBalance().compareTo(BigDecimal.ZERO) > 0) {

            Transaction tx = this.ledgerService.deposit(
                    saved.getId(),
                    request.getInitialBalance()
            );

            this.ledgerRepository.saveAll(tx.getEntries());
        }

        BigDecimal balance = this.getBalance(saved.getId());

        return this.mapper.toResponse(saved, balance);
    }

    // ============================
    // DEPOSIT
    // ============================

    @Transactional
    public WalletResponse deposit(Long id, WalletOperationRequest request, Long expectedVersion) {

        Wallet wallet = this.findWallet(id);

        this.validateVersion(wallet, expectedVersion);

        try {

            Transaction tx = this.ledgerService.deposit(id, request.getAmount());

            this.ledgerRepository.saveAll(tx.getEntries());

        } catch (DomainException ex) {
            throw WalletExceptionTranslator.translate(ex);
        }

        this.cache.evict(id);

        BigDecimal balance = this.getBalance(id);

        return this.mapper.toResponse(wallet, balance);
    }

    // ============================
    // WITHDRAW
    // ============================

    @Transactional
    public WalletResponse withdraw(Long id, WalletOperationRequest request, Long expectedVersion) {

        Wallet wallet = this.findWallet(id);
        this.validateVersion(wallet, expectedVersion);

        BigDecimal balance = this.getBalance(id);

        try {

            this.antiFraudService.validate(
                    request.getAmount(),
                    this.ledgerRepository.findByWalletId(id)
            );

            Transaction tx = this.ledgerService.withdraw(
                    id,
                    request.getAmount(),
                    balance
            );

            this.ledgerRepository.saveAll(tx.getEntries());

        } catch (DomainException ex) {
            throw WalletExceptionTranslator.translate(ex);
        }

        this.cache.evict(id);

        BigDecimal newBalance = this.getBalance(id);

        return this.mapper.toResponse(wallet, newBalance);
    }

    // ============================
    // TRANSFER
    // ============================

    @Transactional
    public WalletResponse transfer(Long fromId, WalletTransferRequest request, Long expectedVersion) {

        Wallet from = this.findWallet(fromId);
        Wallet to = this.findWallet(request.getTargetWalletId());

        this.validateVersion(from, expectedVersion);

        BigDecimal balance = this.getBalance(fromId);

        try {

            this.antiFraudService.validate(
                    request.getAmount(),
                    this.ledgerRepository.findByWalletId(fromId)
            );

            Transaction tx = this.ledgerService.transfer(
                    fromId,
                    request.getTargetWalletId(),
                    request.getAmount(),
                    balance
            );

            this.ledgerRepository.saveAll(tx.getEntries());

        } catch (DomainException ex) {
            throw WalletExceptionTranslator.translate(ex);
        }

        this.cache.evict(fromId);
        this.cache.evict(request.getTargetWalletId());

        BigDecimal newBalance = this.getBalance(fromId);

        return this.mapper.toResponse(from, newBalance);
    }

    // ============================
    // BALANCE
    // ============================

    public WalletResponse getBalanceById(Long id) {

        Wallet wallet = this.findWallet(id);

        BigDecimal balance = this.getBalance(id);

        return this.mapper.toResponse(wallet, balance);
    }

    // ============================
    // INTERNALS
    // ============================

    private Wallet findWallet(Long id) {

        return this.walletRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                WalletErrorType.WALLET_NOT_FOUND,
                                "Wallet not found"
                        )
                );
    }

    /**
     * Valida controle de concorrência via versão (ETag).
     *
     * @param wallet entidade atual
     * @param expectedVersion versão esperada (If-Match)
     */
    private void validateVersion(Wallet wallet, Long expectedVersion) {

        if (expectedVersion == null) {
            throw new BusinessException(
                    TechnicalErrorType.INVALID_PARAMETER,
                    "Missing If-Match header"
            );
        }

        if (!wallet.getVersion().equals(expectedVersion)) {
            throw new BusinessException(
                    TechnicalErrorType.CONFLICT,
                    "Version mismatch"
            );
        }
    }

    private BigDecimal getBalance(Long walletId) {

        BigDecimal cached = this.cache.get(walletId);

        if (cached != null) {
            return cached;
        }

        List<LedgerEntry> entries =
                this.ledgerRepository.findByWalletId(walletId);

        BigDecimal balance =
                new Wallet().calculateBalance(entries);

        this.cache.put(walletId, balance);

        return balance;
    }
}