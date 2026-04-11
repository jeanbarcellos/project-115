package com.jeanbarcellos.project115.wallet.application.mapper;

import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.domain.Wallet;

public class WalletMapper {

    private WalletMapper() {}

    public static WalletResponse toResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .balance(wallet.getBalance())
                .build();
    }
}