package com.jeanbarcellos.project115.wallet.application.service;

import org.springframework.stereotype.Service;

import com.jeanbarcellos.project115.wallet.application.command.WalletCommand;
import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.application.handler.WalletCommandHandler;
import com.jeanbarcellos.project115.wallet.application.handler.WalletCommandHandlerRegistry;

import lombok.RequiredArgsConstructor;

/**
 * Write model (CQRS).
 */
@Service
@RequiredArgsConstructor
public class WalletCommandService {

    private final WalletCommandHandlerRegistry registry;

    public <T extends WalletCommand> WalletResponse execute(T command) {

        WalletCommandHandler<T> handler = this.registry.getHandler(command);

        return handler.handle(command);
    }

}