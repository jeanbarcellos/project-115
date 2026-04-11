package com.jeanbarcellos.project115.wallet.adapter.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jeanbarcellos.project115.wallet.application.dto.WalletCreateRequest;
import com.jeanbarcellos.project115.wallet.application.dto.WalletOperationRequest;
import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.application.dto.WalletTransferRequest;
import com.jeanbarcellos.project115.wallet.application.service.WalletService;

import lombok.RequiredArgsConstructor;

/**
 * Controller REST com suporte a ETag.
 */
@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService service;

    // ============================
    // QUERY
    // ============================

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // ============================
    // CREATE
    // ============================

    @PostMapping
    public ResponseEntity<WalletResponse> create(
            @RequestBody WalletCreateRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    // ============================
    // DEPOSIT
    // ============================

    @PostMapping("/{id}/deposit")
    public ResponseEntity<WalletResponse> deposit(
            @PathVariable Long id,
            @RequestHeader("If-Match") Long version,
            @RequestBody WalletOperationRequest request) {

        WalletResponse response = service.deposit(id, request, version);

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
            @RequestBody WalletOperationRequest request) {

        WalletResponse response = service.withdraw(id, request, version);

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
            @RequestBody WalletTransferRequest request) {

        WalletResponse response = service.transfer(id, request, version);

        return ResponseEntity.ok()
                .eTag(response.getVersion().toString())
                .body(response);
    }

    // ============================
    // BALANCE
    // ============================

    @GetMapping("/{id}/balance")
    public ResponseEntity<WalletResponse> balance(@PathVariable Long id) {
        return ResponseEntity.ok(service.getBalanceById(id));
    }
}