package com.jeanbarcellos.project115.wallet.application.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.application.mapper.WalletMapper;
import com.jeanbarcellos.project115.wallet.application.repository.LedgerEntryRepository;
import com.jeanbarcellos.project115.wallet.application.repository.WalletRepository;
import com.jeanbarcellos.project115.wallet.domain.LedgerEntry;
import com.jeanbarcellos.project115.wallet.domain.Wallet;

import lombok.RequiredArgsConstructor;

/**
 * Read model separado.
 */
@Service
@RequiredArgsConstructor
public class WalletQueryService {

    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final WalletMapper walletMapper;

    public List<WalletResponse> findAll() {

        return this.walletRepository.findAll()
                .stream()
                .map(wallet ->
                        this.walletMapper.toResponse(wallet, this.getBalance(wallet.getId())))
                .toList();
    }

    public WalletResponse findById(Long walletId) {

        Wallet wallet = this.findWallet(walletId);

        return this.walletMapper.toResponse(wallet, this.getBalance(walletId));
    }

    public WalletResponse getBalanceById(Long walletId) {

        Wallet wallet = this.findWallet(walletId);

        BigDecimal balance = this.getBalance(walletId);

        return this.walletMapper.toResponse(wallet, balance);
    }

    private Wallet findWallet(Long id) {

        return this.walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    private BigDecimal getBalance(Long walletId) {

        List<LedgerEntry> entries =
                this.ledgerEntryRepository.findByWalletId(walletId);

        return new Wallet().calculateBalance(entries);
    }
}