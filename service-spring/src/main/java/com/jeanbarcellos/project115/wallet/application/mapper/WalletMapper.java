package com.jeanbarcellos.project115.wallet.application.mapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.domain.Wallet;

@Component
public class WalletMapper {

    public WalletResponse toResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                // .balance(wallet.getBalance())
                .version(wallet.getVersion())
                .build();
    }

    public WalletResponse toResponse(Wallet wallet, BigDecimal balance) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .balance(balance)
                .version(wallet.getVersion())
                .build();
    }

    public List<WalletResponse> toResponseList(List<Wallet> users) {
        if (ObjectUtils.isEmpty(users)) {
            return Collections.emptyList();
        }

        return users
                .stream()
                .map(this::toResponse)
                .toList();
    }
}