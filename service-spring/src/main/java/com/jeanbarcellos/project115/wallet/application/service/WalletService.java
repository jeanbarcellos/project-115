package com.jeanbarcellos.project115.wallet.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jeanbarcellos.core.error.TechnicalErrorType;
import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.wallet.application.dto.WalletCreateRequest;
import com.jeanbarcellos.project115.wallet.application.dto.WalletOperationRequest;
import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.application.error.WalletErrorType;
import com.jeanbarcellos.project115.wallet.application.mapper.WalletExceptionTranslator;
import com.jeanbarcellos.project115.wallet.application.mapper.WalletMapper;
import com.jeanbarcellos.project115.wallet.application.repository.WalletRepository;
import com.jeanbarcellos.project115.wallet.domain.Wallet;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository repository;
    private final WalletMapper mapper;

    // Listagem
        public List<WalletResponse> findAll() {
        return this.repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public WalletResponse findById(Long id) {
        Wallet wallet = this.findWallet(id);

        return mapper.toResponse(wallet);
    }

    // Operacoes

    public WalletResponse create(WalletCreateRequest request) {

        Wallet wallet = new Wallet(request.getInitialBalance());

        return this.mapper.toResponse(repository.save(wallet));
    }

    public WalletResponse deposit(Long id, WalletOperationRequest request) {

        Wallet wallet = this.findWallet(id);

        try {
            wallet.deposit(request.getAmount());
        } catch (DomainException ex) {
            throw WalletExceptionTranslator.translate(ex);
        }

        return this.mapper.toResponse(repository.save(wallet));
    }

    public WalletResponse withdraw(Long id, WalletOperationRequest request) {

        Wallet wallet = this.findWallet(id);

        try {
            wallet.withdraw(request.getAmount());
        } catch (DomainException ex) {
            throw WalletExceptionTranslator.translate(ex);
        }

        return this.mapper.toResponse(repository.save(wallet));
    }

    private Wallet findWallet(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                WalletErrorType.WALLET_NOT_FOUND,
                                "Wallet not found"
                        )
                );
    }

    public WalletResponse withdrraw(Long id, WalletOperationRequest request, Long expectedVersion) {

        Wallet wallet = this.findWallet(id);

        if (!wallet.getVersion().equals(expectedVersion)) {
            throw new BusinessException(
                    TechnicalErrorType.CONFLICT,
                    "Version mismatch");
        }

        try {
            wallet.withdraw(request.getAmount());
        } catch (DomainException ex) {
            throw WalletExceptionTranslator.translate(ex);
        }

        return this.mapper.toResponse(repository.save(wallet));
    }
}