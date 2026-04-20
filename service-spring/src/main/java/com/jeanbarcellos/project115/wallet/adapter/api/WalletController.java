package com.jeanbarcellos.project115.wallet.adapter.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jeanbarcellos.project115.wallet.application.command.DepositCommand;
import com.jeanbarcellos.project115.wallet.application.command.TransferCommand;
import com.jeanbarcellos.project115.wallet.application.command.WithdrawCommand;
import com.jeanbarcellos.project115.wallet.application.dto.WalletOperationRequest;
import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.application.dto.WalletTransferRequest;
import com.jeanbarcellos.project115.wallet.application.service.WalletCommandService;
import com.jeanbarcellos.project115.wallet.application.service.WalletQueryService;

import lombok.RequiredArgsConstructor;

/**
 * Controller REST da Wallet (CQRS).
 */
@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletCommandService commandService;
    private final WalletQueryService queryService;

    // ============================
    // QUERY
    // ============================

    @GetMapping
    public ResponseEntity<List<WalletResponse>> findAll() {
        return ResponseEntity.ok(this.queryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(this.queryService.findById(id));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<WalletResponse> getBalance(@PathVariable Long id) {
        return ResponseEntity.ok(this.queryService.getBalanceById(id));
    }

    // ============================
    // CREATE
    // ============================

    // @PostMapping
    // public ResponseEntity<WalletResponse> create(
    //         @RequestBody WalletCreateRequest request
    //     ) {
    //     return ResponseEntity.ok(this.commandService.create(request));
    // }

    // ============================
    // DEPOSIT
    // ============================

    @PostMapping("/{id}/deposit")
    public ResponseEntity<WalletResponse> deposit(
            @PathVariable Long id,
            @RequestHeader("If-Match") Long version,
            @RequestHeader(value = "Idempotency-Key", required = false) String key,
            @RequestBody WalletOperationRequest request) {

        DepositCommand command = new DepositCommand(
                id,
                request.getAmount(),
                version,
                key
        );

        WalletResponse response = commandService.execute(command);

        return ResponseEntity.ok()
                .eTag(response.getVersion().toString())
                .body(response);
    }

    // ============================
    // WITHDRAW
    // ============================

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<WalletResponse> withdraw(
            @PathVariable Long id,
            @RequestHeader("If-Match") Long version,
            @RequestHeader(value = "Idempotency-Key", required = false) String key,
            @RequestBody WalletOperationRequest request) {

        WithdrawCommand command = new WithdrawCommand(
                id,
                request.getAmount(),
                version,
                key
        );

        WalletResponse response = this.commandService.execute(command);

        return ResponseEntity.ok()
                .eTag(response.getVersion().toString())
                .body(response);
    }

    // ============================
    // TRANSFER
    // ============================

    @PostMapping("/{id}/transfer")
    public ResponseEntity<WalletResponse> transfer(
            @PathVariable Long id,
            @RequestHeader("If-Match") Long version,
            @RequestHeader(value = "Idempotency-Key", required = false) String key,
            @RequestBody WalletTransferRequest request) {

        TransferCommand command = new TransferCommand(
                id,
                request.getTargetWalletId(),
                request.getAmount(),
                version,
                key
        );
        WalletResponse response = this.commandService.execute(command);

        return ResponseEntity.ok()
                .eTag(response.getVersion().toString())
                .body(response);
    }

}