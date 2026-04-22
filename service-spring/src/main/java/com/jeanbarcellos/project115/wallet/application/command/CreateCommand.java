package com.jeanbarcellos.project115.wallet.application.command;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommand {

    private BigDecimal initialBalance;

}