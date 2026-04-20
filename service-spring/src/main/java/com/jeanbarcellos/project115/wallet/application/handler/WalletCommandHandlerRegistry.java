package com.jeanbarcellos.project115.wallet.application.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.jeanbarcellos.project115.wallet.application.command.WalletCommand;

@Component
public class WalletCommandHandlerRegistry {

    private final Map<Class<?>, WalletCommandHandler<?>> handlers = new HashMap<>();

    public WalletCommandHandlerRegistry(List<WalletCommandHandler<?>> handlerList) {

        for (WalletCommandHandler<?> handler : handlerList) {
            this.handlers.put(handler.getCommandType(), handler);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends WalletCommand> WalletCommandHandler<T> getHandler(T command) {

        WalletCommandHandler<T> handler =
                (WalletCommandHandler<T>) this.handlers.get(command.getClass());

        if (handler == null) {
            throw new IllegalArgumentException("No handler for " + command.getClass());
        }

        return handler;
    }
}