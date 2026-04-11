package com.jeanbarcellos.project115.wallet.adapter.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jeanbarcellos.project115.wallet.application.dto.WalletCreateRequest;
import com.jeanbarcellos.project115.wallet.application.dto.WalletOperationRequest;
import com.jeanbarcellos.project115.wallet.application.dto.WalletResponse;
import com.jeanbarcellos.project115.wallet.application.service.WalletService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService service;

    @PostMapping
    public ResponseEntity<WalletResponse> create(
            @RequestBody WalletCreateRequest request) {
        return ResponseEntity.status(201).body(service.create(request));
    }

    @PostMapping("/{id}/deposit")
    public WalletResponse deposit(
            @PathVariable Long id,
            @RequestBody WalletOperationRequest request) {
        return service.deposit(id, request);
    }

    @PostMapping("/{id}/withdraw")
    public WalletResponse withdraw(
            @PathVariable Long id,
            @RequestBody WalletOperationRequest request) {
        return service.withdraw(id, request);
    }
}