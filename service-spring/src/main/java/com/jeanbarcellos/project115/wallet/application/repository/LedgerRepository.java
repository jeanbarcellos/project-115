package com.jeanbarcellos.project115.wallet.application.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jeanbarcellos.project115.wallet.domain.LedgerEntry;

public interface LedgerRepository extends JpaRepository<LedgerEntry, UUID> {

    List<LedgerEntry> findByWalletId(Long walletId);
}