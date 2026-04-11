package com.jeanbarcellos.project115.wallet.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jeanbarcellos.project115.wallet.domain.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}