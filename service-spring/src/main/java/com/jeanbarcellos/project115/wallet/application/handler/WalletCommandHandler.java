package com.jeanbarcellos.project115.wallet.application.handler;

import com.jeanbarcellos.project115.wallet.application.command.WalletCommand;
import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;

public interface WalletCommandHandler<T extends WalletCommand> {

    Class<T> getCommandType();

    WalletResponse handle(T command);
}