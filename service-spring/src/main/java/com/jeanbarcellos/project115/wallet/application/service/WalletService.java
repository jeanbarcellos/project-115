package com.jeanbarcellos.project115.wallet.application.service;

import org.springframework.stereotype.Service;

import com.jeanbarcellos.core.exception.BusinessException;
import com.jeanbarcellos.core.exception.DomainException;
import com.jeanbarcellos.project115.wallet.application.dto.WalletCreateRequest;
import com.jeanbarcellos.project115.wallet.application.dto.WalletOperationRequest;
import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.application.error.WalletErrorType;
import com.jeanbarcellos.project115.wallet.application.mapper.WalletExceptionTranslator;
import com.jeanbarcellos.project115.wallet.application.mapper.WalletMapper;
import com.jeanbarcellos.project115.wallet.domain.Wallet;
import com.jeanbarcellos.project115.wallet.domain.WalletRepository;

@Service
public class WalletService {

    private final WalletRepository repository;

    public WalletService(WalletRepository repository) {
        this.repository = repository;
    }

    public WalletResponse create(WalletCreateRequest request) {

        Wallet wallet = new Wallet(request.getInitialBalance());

        return WalletMapper.toResponse(repository.save(wallet));
    }

    public WalletResponse deposit(Long id, WalletOperationRequest request) {

        Wallet wallet = findWallet(id);

        try {
            wallet.deposit(request.getAmount());
        } catch (DomainException ex) {
            throw WalletExceptionTranslator.translate(ex);
        }

        return WalletMapper.toResponse(repository.save(wallet));
    }

    public WalletResponse withdraw(Long id, WalletOperationRequest request) {

        Wallet wallet = findWallet(id);

        try {
            wallet.withdraw(request.getAmount());
        } catch (DomainException ex) {
            throw WalletExceptionTranslator.translate(ex);
        }

        return WalletMapper.toResponse(repository.save(wallet));
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
}